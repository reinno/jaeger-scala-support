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
  protected val httpHeaderTag: String = "uber-trace-id"

  protected val serviceNam: String
  protected lazy val tracer: Tracer = TraceSupport.defaultTracer(serviceNam)
}

