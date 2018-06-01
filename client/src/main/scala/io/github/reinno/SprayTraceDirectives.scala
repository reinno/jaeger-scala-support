package io.github.reinno

import java.util.Collections

import io.opentracing.Span
import io.opentracing.Tracer.SpanBuilder
import io.opentracing.propagation.Format.Builtin.HTTP_HEADERS
import io.opentracing.propagation.TextMapExtractAdapter
import io.opentracing.tag.Tags
import shapeless.HNil
import spray.http.HttpHeader
import spray.routing._

import scala.concurrent.ExecutionContext

trait SprayTraceDirectives extends TraceSupport {
  implicit val exec: ExecutionContext

  import spray.routing.directives.BasicDirectives._

  def getSpanBuilder(ctx: RequestContext): SpanBuilder = {
    tracer.buildSpan(ctx.request.uri.path.toString())
      .withTag(Tags.SPAN_KIND.getKey, Tags.SPAN_KIND_SERVER)
      .withTag(Tags.COMPONENT.getKey, "Router")
      .withTag(Tags.HTTP_METHOD.getKey, ctx.request.method.value)
      .withTag(ExtTags.HTTP_REQUEST.getKey, ctx.request.entity.toString)
  }

  def withTrace: Directive0 =
    mapInnerRoute { inner ⇒ ctx ⇒
      getSpan(ctx)
      inner(ctx)
    }

  def withTraceCtx: Directive1[TraceContext] = new Directive1[TraceContext] {
    override def happly(f: shapeless.::[TraceContext, shapeless.HNil] => Route): Route = {
      ctx ⇒
        val span: Span = getSpan(ctx)
        f(TraceContext(tracer, span, traceConfig) :: HNil)(ctx)
    }
  }

  private def getParentSpanContext(ctx: RequestContext) = {
    ctx.request.header.collectFirst {
      case HttpHeader(`httpHeaderTag`, value) =>
        val m = Collections.singletonMap(httpHeaderTag, value)
        tracer.extract(HTTP_HEADERS, new TextMapExtractAdapter(m))
    }
  }

  private def getSpan(ctx: RequestContext) = {
    val spanBuilder = getSpanBuilder(ctx)
    val parentCtx = getParentSpanContext(ctx)
    val span = parentCtx
      .map(spanBuilder.asChildOf).getOrElse(spanBuilder)
      .start()

    ctx.withHttpResponseMapped {
      rsp =>
        span
          .setTag(ExtTags.HTTP_RESPONSE.getKey, rsp.entity.asString)
          .setTag(ExtTags.HTTP_STATUS_CODE.getKey, rsp.status.value)
        span.finish()
        rsp
    }
    span
  }
}

