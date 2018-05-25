package io.reinno

import io.jaegertracing.Tracer
import io.jaegertracing.reporters.RemoteReporter
import io.jaegertracing.senders.UdpSender

trait TraceSupport {
  val traceConfig: TraceConfig

  protected lazy val httpHeaderTag: String = traceConfig.headerTag
  protected lazy val tracer: Tracer =
    new Tracer.Builder("demo")
      .withReporter(
        new RemoteReporter.Builder()
          .withSender(new UdpSender(traceConfig.address, traceConfig.port, 0))
          .build())
      .build()
}

