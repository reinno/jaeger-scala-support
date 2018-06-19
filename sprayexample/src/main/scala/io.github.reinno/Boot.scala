package io.github.reinno

import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }
import akka.io.IO
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("jaeger-spray-example")

  /* Use Akka to create our Spray Service */
  val service = system.actorOf(Props[SpraySampleActor], "spray-sample-service")

  /* and bind to Akka's I/O interface */
  IO(Http) ! Http.Bind(service, "localhost", 9002)
}

/* Our Server Actor is pretty lightweight; simply mixing in our route trait and logging */
class SpraySampleActor extends Actor with Routes with ActorLogging {
  def actorRefFactory = context
  def receive = runRoute(spraysampleRoute)
}