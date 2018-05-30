package io.github.reinno

import java.util.Collections

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server.RouteResult.{ Complete, Rejected }
import akka.http.scaladsl.server._
import io.opentracing.Tracer.SpanBuilder
import io.opentracing.propagation.Format.Builtin.HTTP_HEADERS
import io.opentracing.propagation.TextMapExtractAdapter
import io.opentracing.tag.Tags

import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success }

trait AkkaHttpTraceDirectives extends TraceSupport {
  implicit val exec: ExecutionContext

  def getSpanBuilder(ctx: RequestContext): SpanBuilder = {
    tracer.buildSpan(ctx.request.getUri.getPathString)
      .withTag(Tags.SPAN_KIND.getKey, Tags.SPAN_KIND_SERVER)
      .withTag(Tags.COMPONENT.getKey, "Router")
      .withTag(Tags.HTTP_METHOD.getKey, ctx.request.method.value)
      .withTag(ExtTags.HTTP_REQUEST.getKey, ctx.request.entity.toString)
  }

  def withTrace: Directive0 = Directive { inner =>
    withTraceCtx(_ => ctx => inner(())(ctx))
  }

  def withTraceCtx: Directive1[TraceContext] = Directive { inner => ctx =>
    val spanBuilder = getSpanBuilder(ctx)
    val parentCtx = getParentSpanContext(ctx)
    val span = parentCtx
      .map(spanBuilder.asChildOf).getOrElse(spanBuilder)
      .start()

    val routerResult = inner(Tuple1(TraceContext(tracer, span, traceConfig)))(ctx)
    routerResult.onComplete { res =>
      res match {
        case Success(Complete(result)) =>
          span
            .setTag(ExtTags.HTTP_RESPONSE.getKey, result.entity.toString)
            .setTag(ExtTags.HTTP_STATUS_CODE.getKey, result.status.value)
        case Success(Rejected(result)) =>
          span
            .setTag(Tags.ERROR.getKey, result.toString())
        case Failure(ex) =>
      }

      span.finish()
    }
    routerResult
  }

  private def getParentSpanContext(ctx: RequestContext) = {
    ctx.request.header.collectFirst {
      case HttpHeader(`httpHeaderTag`, value) =>
        val m = Collections.singletonMap(httpHeaderTag, value)
        tracer.extract(HTTP_HEADERS, new TextMapExtractAdapter(m))
    }
  }
}
