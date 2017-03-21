package co.s4n.users.services

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes, StatusCode}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import co.s4n.users.persistance.User
import io.circe.Json
import com.outworkers.phantom.dsl.ResultSet
import scala.concurrent.Future
import co.s4n.users.persistance.UserRepository
import scala.concurrent.ExecutionContext
import Responsable._

class UsersRoute(userRepo: UserRepository)(implicit ec: ExecutionContext){
  def handle[T](arg: Future[T])(implicit resp: Responsable[T]) = {
    resp.toResponse(arg)
  }

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
