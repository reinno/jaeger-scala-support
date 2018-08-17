package io.github.reinno

trait TraceConfig {
  val headerTag: String = "uber-trace-id"
  val serviceName: String
  //udp or http
  val protocol: String
  val address: String
  val port: Int
  val httpEndpoint: String
  // switch
  // prefix filter
  // record request/response in route
  val maxPacketSize: Int
}

case class TraceConfigLocal(
  serviceName: String,
  protocol: String = "http",
  address: String = "localhost",
  port: Int = 6831,
  httpEndpoint: String = "http://localhost:14268/api/traces",
  maxPacketSize: Int = 10485760) extends TraceConfig

