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

  val spraySampleRoute: Route = withTraceCtx() {
    traceCtx =>
      {
        path("entity") {
          get {
            complete {
              val span = traceCtx.tracer.buildSpan("get entity")
                .asChildOf(traceCtx.span).start()
              val result = List(Foo("foo1"), Foo("foo2"))
              span.finish
              result
            }
          } ~ post {
            respondWithStatus(Created) {
              entity(as[Foo]) { someObject =>
                doCreate(someObject)
              }
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
