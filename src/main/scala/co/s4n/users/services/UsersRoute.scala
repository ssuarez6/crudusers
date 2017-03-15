package co.s4n.users.services

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._
import co.s4n.users.persistance.entity.User
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait Message
case class MsgSuccess(success: Boolean, message: String) extends Message
case class MsgUser(user: User) extends Message

class UsersRoute{
  val route = path("users" / IntNumber) {
    userId => {
      get {
        val res: Future[Option[User]] = UserService.getUserById(userId).map(x => {
          val user: User = x.getOrElse(User(-1, "__invalid"))
          if(user.id == -1 && user.username == "__invalid"){
            MsgSuccess(false, "user could not be found").asJson.toString
          }else{
            MsgUser(user).asJson.toString
          }
        }).recover {
          case _ => {
            MsgSuccess(false, "internal server problem").asJson.toString
          }
        }  
        complete(res)
      } ~ delete {
        val res = UserService.deleteById(userId)
        val res2: Future[String] = res.map(x =>{
          println("WHEN DELETING:")
          println(x)
          println(x.toString)
          MsgSuccess(true, "user deleted successfully").asJson.toString
          }).recover {
            case _ => MsgSuccess(false, "internal server problem").asJson.toString
          }
        complete(res2)
      }
    }
  } ~ path("users"){
    post {
      entity(as[User]) { capsule =>
        case class Msg(success: Boolean, message: String)
        val res = UserService.saveOrUpdate(capsule)
        val msg: Future[String] = res.map(x =>{
          MsgSuccess(true, s"user saved successfully\n${x.toString}")
            .asJson
            .toString
            }).recover {
              case _ => MsgSuccess(false, "internal server problem").asJson.toString
            }
        complete(msg)
      }
    } ~ put {
      entity(as[User]){ user =>
        case class Msg(success: Boolean, message: String)
        val res = UserService.saveOrUpdate(user)
        val msg: Future[String] = res.map(x =>{
          MsgSuccess(true, s"user updated successfully\n${x.toString}")
            .asJson
            .toString
            }).recover{
              case _ => MsgSuccess(false, "internal server problem").asJson.toString
            }
        complete(msg)
      }
    }
  }

  val route2 = path("test"){
    get{
      val fut: Future[HttpResponse] =
        Future(HttpResponse(StatusCodes.OK, MsgSuccess(true, "afjañsdlfj").asJson.toString))
      complete(StatusCodes.OK)
    }
  }
}
