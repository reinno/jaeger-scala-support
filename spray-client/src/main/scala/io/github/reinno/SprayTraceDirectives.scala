/*
 * Copyright 2018 the jaeger scala support contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.reinno

import java.util.Collections

import io.opentracing.Tracer.SpanBuilder
import io.opentracing.propagation.Format.Builtin.HTTP_HEADERS
import io.opentracing.propagation.TextMapExtractAdapter
import io.opentracing.tag.{ StringTag, Tags }
import shapeless.HNil
import spray.http.HttpHeader
import spray.routing._

import scala.concurrent.ExecutionContext

trait SprayTraceDirectives extends TraceSupport {
  implicit val exec: ExecutionContext

  import spray.routing.directives.BasicDirectives._

  private def getSpanBuilder(ctx: RequestContext, customTags: Map[StringTag, String]): SpanBuilder = {
    val spanBuilder = tracer.buildSpan(ctx.request.uri.path.toString())
      .withTag(Tags.SPAN_KIND.getKey, Tags.SPAN_KIND_SERVER)
      .withTag(Tags.COMPONENT.getKey, "HttpRoute")
      .withTag(Tags.HTTP_URL.getKey, ctx.request.uri.toString)
      .withTag(Tags.HTTP_METHOD.getKey, ctx.request.method.value)
      .withTag(ExtTags.HTTP_REQUEST.getKey, ctx.request.entity.asString)

    customTags.foldLeft(spanBuilder) {
      case (builder, (tag, value)) =>
        builder.withTag(tag.getKey, value)
    }
  }

  def withTrace(customTags: Map[StringTag, String] = Map.empty): Directive0 =
    mapInnerRoute { inner ⇒ ctx ⇒
      val (_, ctxNew) = getSpan(ctx, customTags)
      inner(ctxNew)
    }

  def withTraceCtx(customTags: Map[StringTag, String] = Map.empty): Directive1[TraceContext] = new Directive1[TraceContext] {
    override def happly(f: shapeless.::[TraceContext, shapeless.HNil] => Route): Route = {
      ctx ⇒
        val (span, ctxNew) = getSpan(ctx, customTags)
        f(TraceContext(tracer, span, traceConfig) :: HNil)(ctxNew)
    }
  }

  private def getParentSpanContext(ctx: RequestContext) = {
    ctx.request.header.collectFirst {
      case HttpHeader(`httpHeaderTag`, value) =>
        val m = Collections.singletonMap(httpHeaderTag, value)
        tracer.extract(HTTP_HEADERS, new TextMapExtractAdapter(m))
    }
  }

  private def getSpan(ctx: RequestContext, customTags: Map[StringTag, String]) = {
    val spanBuilder = getSpanBuilder(ctx, customTags)
    val parentCtx = getParentSpanContext(ctx)
    val span = parentCtx
      .map(spanBuilder.asChildOf).getOrElse(spanBuilder)
      .start()

    val ctxNew = ctx.withHttpResponseMapped {
      rsp =>
        span
          .setTag(ExtTags.HTTP_RESPONSE.getKey, rsp.entity.asString)
          .setTag(ExtTags.HTTP_STATUS_CODE.getKey, rsp.status.value)
        span.finish()
        rsp
    }
    (span, ctxNew)
  }
}

