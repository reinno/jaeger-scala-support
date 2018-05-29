package io.github.reinno

import io.opentracing.{ Span, Tracer }

case class TraceContext(tracer: Tracer, span: Span, cfg: TraceConfig)
