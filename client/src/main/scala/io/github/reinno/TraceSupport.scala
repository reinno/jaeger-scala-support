package io.github.reinno

import io.jaegertracing.Tracer
import io.jaegertracing.reporters.RemoteReporter
import io.jaegertracing.senders.{ HttpSender, ThriftSender, UdpSender }

object TraceSupport {
  lazy val localUdpSender: ThriftSender =
    new UdpSender("localhost", 6831, 0)

  val DefaultMaxPacketSize = 10485760
  lazy val localHttpSender: ThriftSender =
    new HttpSender.Builder("http://localhost:14268/api/traces")
      .withMaxPacketSize(DefaultMaxPacketSize).build()

  def defaultTracer(serviceName: String): Tracer = {
    new Tracer.Builder(serviceName)
      .withReporter(
        new RemoteReporter.Builder().withSender(localHttpSender).build()).build()
  }
}

trait TraceSupport {
  val traceConfig: TraceConfig
  protected lazy val httpHeaderTag: String = traceConfig.headerTag

  protected lazy val tracer: Tracer =
    TraceSupport.defaultTracer(traceConfig.serviceName)
}

