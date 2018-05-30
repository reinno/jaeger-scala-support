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

  def getSpanBuilder(ctx: RequestContext): SpanBuilder = {
    tracer.buildSpan(ctx.request.uri.path.toString())
      .withTag(Tags.SPAN_KIND.getKey, Tags.SPAN_KIND_SERVER)
      .withTag(Tags.COMPONENT.getKey, "Router")
      .withTag(Tags.HTTP_METHOD.getKey, ctx.request.method.value)
      .withTag(ExtTags.HTTP_REQUEST.getKey, ctx.request.entity.toString)
  }

  def withTrace: Directive0 = new Directive[HNil] {
    override def happly(f: HNil => Route): Route = {
      ctx ⇒
        val span: Span = getSpan(ctx)
        f(HNil)(ctx)
        span.finish()
    }
  }

  def withTraceCtx: Directive1[TraceContext] = new Directive1[TraceContext] {
    override def happly(f: shapeless.::[TraceContext, shapeless.HNil] => Route): Route = {
      ctx ⇒
        val span: Span = getSpan(ctx)
        // Spray does not support call back before send, so need service call finish
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
    span
  }
}

