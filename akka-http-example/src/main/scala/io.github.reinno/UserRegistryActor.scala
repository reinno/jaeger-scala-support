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

import akka.actor.{ Actor, ActorLogging, Props }

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])

object UserRegistryActor {

  final case class ActionPerformed(description: String)

  final case class GetUsers(traceCtx: TraceContext)

  final case class CreateUser(user: User)

  final case class GetUser(name: String)

  final case class DeleteUser(name: String)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {

  import UserRegistryActor._

  var users = Set.empty[User]

  def receive: Receive = {
    case GetUsers(traceCtx) =>
      val span = traceCtx.tracer.buildSpan("getUserFromDb")
        .asChildOf(traceCtx.span).start()
      sender() ! Users(users.toSeq)
      span.finish()

    case CreateUser(user) =>
      users += user
      sender() ! ActionPerformed(s"User ${user.name} created.")
    case GetUser(name) =>
      sender() ! users.find(_.name == name)
    case DeleteUser(name) =>
      users.find(_.name == name) foreach { user => users -= user }
      sender() ! ActionPerformed(s"User $name deleted.")
  }
}
