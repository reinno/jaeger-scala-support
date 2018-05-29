package io.github.reinno.examples.akkahttp

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import io.github.reinno.examples.akkahttp.UserRegistryActor.ActionPerformed
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
