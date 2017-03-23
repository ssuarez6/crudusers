package co.s4n.users.services

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import co.s4n.users.persistance.{User, UserRepository}
import scala.concurrent.{Future, ExecutionContext}
import Responsable._

class UsersRoute(userRepo: UserRepository)(implicit ec: ExecutionContext){
  def handle[T](arg: Future[T])(implicit resp: Responsable[T]) = {
    resp.toResponse(arg)
  }

  val mp = new MessageProducer

  val route = path("users" / Segment) {
    username => {
      get {
        complete(handle(userRepo.getUserByUsername(username)))
        } ~ delete {
          complete(handle(userRepo.deleteByUsername(username))) 
        }
    }
  } ~ path("users"){
    post {
      entity(as[User]) { user =>
        mp.produceMessage("users-creation", user.toString)
        complete(handle(userRepo.saveOrUpdate(user)))
      }
      } ~ put {
        entity(as[User]){ user =>
          complete(handle(userRepo.saveOrUpdate(user)))
        }
        } ~ get {
          complete(handle(userRepo.getUsers))
        }
  } 
}
