package io.reinno

import java.util.Collections

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server._
import io.opentracing.{ SpanContext, Tracer }
import io.opentracing.propagation.Format.Builtin.HTTP_HEADERS
import io.opentracing.propagation.TextMapExtractAdapter
import io.opentracing.tag.Tags

import scala.concurrent.ExecutionContext

trait TraceDirectives extends TraceSupport {
  implicit val exec: ExecutionContext

  def withTrace: Directive0 = Directive { inner => ctx =>
    val initSpanBuilder = tracer.buildSpan(ctx.request.getUri.getPathString)
      .withTag(Tags.SPAN_KIND.getKey, Tags.SPAN_KIND_SERVER)
      .withTag(Tags.COMPONENT.getKey, "Router")
      .withTag(Tags.HTTP_METHOD.getKey, ctx.request.method.value)

    val spanBuilderWithSuper = ctx.request.header.collectFirst {
      case HttpHeader(`httpHeaderTag`, value) =>
        extractSpan(initSpanBuilder, value)
    }

    val spanBuilder = spanBuilderWithSuper match {
      case Some(childSpan) =>
        childSpan
      case None =>
        initSpanBuilder
    }

    val span = spanBuilder.startActive(true)

    val routerResult = inner(())(ctx)
    routerResult.onComplete { _ =>
      //span.close()
      tracer.activeSpan().finish()
    }
    routerResult
  }

  private def extractSpan(span: Tracer.SpanBuilder, value: String) = {
    val m = Collections.singletonMap(httpHeaderTag, value)

    val extract: SpanContext = tracer.extract(HTTP_HEADERS, new TextMapExtractAdapter(m))
    span.asChildOf(extract)
  }
}
