package io.github.reinno

import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }
import akka.io.IO
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("jaeger-spray-example")

  val service = system.actorOf(Props[SpraySampleActor], "spray-sample-service")

  IO(Http) ! Http.Bind(service, "localhost", 9002)
}

class SpraySampleActor extends Actor with Routes with ActorLogging {
  def actorRefFactory = context
  def receive = runRoute(spraySampleRoute)
}