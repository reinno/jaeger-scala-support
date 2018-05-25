package io.reinno

trait TraceConfig {
  val headerTag: String

  // protocol
  val address: String
  val port: Int
  // switch
  // prefix filter
}

case class TraceConfigLocal(
  headerTag: String = "uber-trace-id",
  address: String = "localhost",
  port: Int = 6831) extends TraceConfig

