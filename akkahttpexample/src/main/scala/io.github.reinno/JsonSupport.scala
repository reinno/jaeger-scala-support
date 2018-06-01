package io.github.reinno

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import io.github.reinno.UserRegistryActor.ActionPerformed

trait JsonSupport extends SprayJsonSupport {

  import spray.json.DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
