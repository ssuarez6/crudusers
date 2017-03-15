package co.s4n.users.services

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._
import co.s4n.users.persistance.User
import com.outworkers.phantom.dsl.ResultSet

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait APIMessage
case class MsgSuccess(success: Boolean, message: String) extends APIMessage
case class MsgUser(user: User) extends APIMessage

class UsersRoute{
  val route = path("users" / IntNumber) {
    userId => {
      get {
        val res: Future[HttpResponse] = UserService.getUserById(userId).map(x => {
          val user: User = x.getOrElse(User(-1, "__invalid"))
          if(user.id == -1 && user.username == "__invalid"){
            HttpResponse(StatusCodes.NotFound, entity = 
              MsgSuccess(false, "user could not be found")
                .asJson
                .toString)
          }else{
            HttpResponse(StatusCodes.OK, entity = 
              MsgUser(user)
                .asJson
                .toString)
          }
        }).recover{
          case _ => HttpResponse(StatusCodes.InternalServerError, entity =
            MsgSuccess(false, "internal server problem")
              .asJson
              .toString)
        }
        complete(res)
      } ~ delete {
        val res = UserService.deleteById(userId)
        val res2: Future[HttpResponse] = res.map(x =>{
          HttpResponse(StatusCodes.OK, entity = 
            MsgSuccess(true, "user deleted successfully")
              .asJson
              .toString)
          }).recover {
            case _ => HttpResponse(StatusCodes.InternalServerError, entity = 
              MsgSuccess(false, "internal server problem")
                .asJson
                .toString)
          }
        complete(res2)
      }
    }
  } ~ path("users"){
    post {
      entity(as[User]) { capsule =>
        case class Msg(success: Boolean, message: String)
        val res = UserService.saveOrUpdate(capsule)
        val msg: Future[HttpResponse] = res.map(x =>{
          HttpResponse(StatusCodes.OK, entity = 
            MsgSuccess(true, s"user saved successfully\n${x.toString}")
              .asJson
              .toString)
            }).recover {
              case _ => HttpResponse(StatusCodes.InternalServerError, entity = 
                MsgSuccess(false, "internal server problem")
                  .asJson
                  .toString)
            }
        complete(msg)
      }
    } ~ put {
      entity(as[User]){ user =>
        val res = UserService.saveOrUpdate(user)
        val msg: Future[HttpResponse] = res.map(x =>{
          HttpResponse(StatusCodes.OK, entity = 
            MsgSuccess(true, s"user updated successfully\n${x.toString}")
              .asJson
              .toString)
            }).recover{
              case _ => HttpResponse(StatusCodes.InternalServerError, entity = 
                MsgSuccess(false, "internal server problem").asJson.toString)
            }
        complete(msg)
      }
    } ~ get {
      val res: Future[HttpResponse] = UserService.getUsers.map(x =>{
        HttpResponse(StatusCodes.OK, entity = x.asJson.toString)
      })
      complete(res)
    }
  }
}
