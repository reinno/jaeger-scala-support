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