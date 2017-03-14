package co.s4n.users.services

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.collection.mutable.ListBuffer
import co.s4n.users.persistance.{User, UsersPersistance}

class UsersRoute(up: UsersPersistance) {
  val route = 
    path("users" / IntNumber) {
      userId => {
        get {
          val res: Option[User] = up.users.find(x => x.id == userId)
          res match {
            case Some(user) => complete(HttpResponse(StatusCodes.OK, entity = user.asJson.toString))
            case _ => complete(StatusCodes.NotFound)
          }
        } ~
        delete {
          case class Msg(success: Boolean, message: String)
          if (up.remove(userId))
            complete(HttpResponse(StatusCodes.OK, entity = Msg(true, "user deleted successfully").asJson.toString))
          else complete(HttpResponse(StatusCodes.NotFound, entity = Msg(false, "user not found on the system").asJson.toString))
        }
      }
    } ~ 
    path("users"){
      post {
        case class Username(username: String)
        entity(as[Username]) {
          capsule =>
        case class Msg(success: Boolean, message: String, users: ListBuffer[User])
        if(up.add(capsule.username)) 
          complete(HttpResponse(StatusCodes.OK, entity = Msg(true, "user added successfully", up.users).asJson.toString))
        else complete(HttpResponse(StatusCodes.Conflict, entity = Msg(false, "username already in use", up.users).asJson.toString))
        }
      } ~
      put {
        entity(as[User]){
          user =>
            val couldUpdate = up.updateById(user)
        case class Msg(success: Boolean, message: String)
        if(couldUpdate){
          complete(HttpResponse(StatusCodes.OK, entity = Msg(true, "user could be updated").asJson.toString))
        }else
          complete(HttpResponse(StatusCodes.NotFound, entity = Msg(false, "user not found to update").asJson.toString))
        }
      } ~
      get {
        case class Msg(users: ListBuffer[User])
        complete(HttpResponse(StatusCodes.OK, entity = Msg(up.users).asJson.toString))
      }
    }
}
