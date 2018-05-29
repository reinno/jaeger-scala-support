package io.github.reinno.examples.akkahttp

//#user-registry-actor
import akka.actor.{ Actor, ActorLogging, Props }
import io.github.reinno.TraceContext

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
      sender() ! ActionPerformed(s"User ${name} deleted.")
  }
}