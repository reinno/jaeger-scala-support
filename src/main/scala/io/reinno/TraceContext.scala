package io.reinno

import io.opentracing.{ SpanContext, Tracer }

case class TraceContext(tracer: Tracer, spanCtx: SpanContext, cfg: TraceConfig)
