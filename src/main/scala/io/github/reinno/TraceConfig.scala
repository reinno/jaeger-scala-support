package io.github.reinno

trait TraceConfig {
  val headerTag: String = "uber-trace-id"
  val serviceName: String
  // protocol
  val address: String
  val port: Int
  // switch
  // prefix filter
  // record request/response in route
}

case class TraceConfigLocal(serviceName: String, address: String = "localhost", port: Int = 6831) extends TraceConfig

