package io.reinno

trait TraceConfig {
  val headerTag: String = "uber-trace-id"

  // protocol
  val address: String
  val port: Int
  // switch
  // prefix filter
}

case class TraceConfigLocal(address: String = "localhost", port: Int = 6831) extends TraceConfig

