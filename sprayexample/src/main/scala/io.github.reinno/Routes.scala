package io.github.reinno

import akka.util.Timeout

import org.json4s.{ DefaultFormats, Formats }
import spray.http.StatusCodes._
import spray.httpx.Json4sSupport
import spray.routing.{ HttpService, Route }

import scala.concurrent.duration._

case class Foo(bar: String)

trait Routes extends HttpService with Json4sSupport with SprayTraceDirectives {
  implicit def json4sFormats: Formats = DefaultFormats

  implicit val exec = actorRefFactory.dispatcher
  override val traceConfig: TraceConfig = TraceConfigLocal("SprayTraceExample")
  implicit val timeout = Timeout(5 seconds)

  val spraysampleRoute: Route = withTrace {
    path("entity") {
      get {
        complete(List(Foo("foo1"), Foo("foo2")))
      } ~ post {
        respondWithStatus(Created) {
          entity(as[Foo]) { someObject =>
            doCreate(someObject)
          }
        }
      }
    }
  }

  def doCreate[T](foo: Foo) = {
    complete {
      ???
    }
  }
}
