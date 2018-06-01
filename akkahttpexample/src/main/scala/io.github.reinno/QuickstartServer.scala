package io.github.reinno

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object QuickstartServer extends App with UserRoutes {

  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override val traceConfig: TraceConfig = TraceConfigLocal("AkkaTraceExample")

  val userRegistryActor: ActorRef = system.actorOf(UserRegistryActor.props, "userRegistryActor")

  lazy val routes: Route = userRoutes

  Http().bindAndHandle(routes, "localhost", 9001)

  println(s"Server online at http://localhost:9001/")

  Await.result(system.whenTerminated, Duration.Inf)
}
