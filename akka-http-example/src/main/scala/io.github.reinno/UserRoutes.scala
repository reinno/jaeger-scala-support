package io.github.reinno

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ delete, get, post }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import io.github.reinno.UserRegistryActor._
import io.opentracing.tag.{ StringTag, Tags }

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

trait UserRoutes extends JsonSupport with AkkaHttpTraceDirectives {
  implicit def system: ActorSystem

  override implicit lazy val exec: ExecutionContext = system.dispatcher

  lazy val log = Logging(system, classOf[UserRoutes])

  def userRegistryActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)
  val customTags: Map[StringTag, String] = Map(ExtTags.VERSION -> "0.1", Tags.COMPONENT -> "sampleRoute")

  lazy val userRoutes: Route = withTraceCtx(customTags) {
    traceCtx =>
      pathPrefix("users") {
        concat(
          pathEnd {
            concat(
              get {
                val users: Future[Users] =
                  (userRegistryActor ? GetUsers(traceCtx)).mapTo[Users]
                complete(users)
              },
              post {
                entity(as[User]) { user =>
                  val userCreated: Future[ActionPerformed] =
                    (userRegistryActor ? CreateUser(user)).mapTo[ActionPerformed]
                  onSuccess(userCreated) { performed =>
                    log.info("Created user [{}]: {}", user.name, performed.description)
                    complete((StatusCodes.Created, performed))
                  }
                }
              })
          },
          path(Segment) { name =>
            concat(
              get {
                val maybeUser: Future[Option[User]] =
                  (userRegistryActor ? GetUser(name)).mapTo[Option[User]]
                rejectEmptyResponse {
                  complete(maybeUser)
                }
              },
              delete {
                val userDeleted: Future[ActionPerformed] =
                  (userRegistryActor ? DeleteUser(name)).mapTo[ActionPerformed]
                onSuccess(userDeleted) { performed =>
                  log.info("Deleted user [{}]: {}", name, performed.description)
                  complete((StatusCodes.OK, performed))
                }
              })
          })
      }
  }

}
