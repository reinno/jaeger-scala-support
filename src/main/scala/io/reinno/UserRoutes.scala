package io.reinno

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.{ ExecutionContext, Future }
import io.reinno.UserRegistryActor._
import akka.pattern.ask
import akka.util.Timeout

trait UserRoutes extends JsonSupport with TraceDirectives {
  implicit def system: ActorSystem

  override implicit lazy val exec: ExecutionContext = system.dispatcher

  lazy val log = Logging(system, classOf[UserRoutes])

  def userRegistryActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)

  lazy val userRoutes: Route = withTraceCtx {
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
