package co.s4n.users.services

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes, StatusCode}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import co.s4n.users.persistance.User
import io.circe.Json
import com.outworkers.phantom.dsl.ResultSet
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import co.s4n.users.persistance.UserRepository

sealed trait AppMsg
case class MsgUser(user: User) extends AppMsg
case class MsgUsers(users: Seq[User]) extends AppMsg
case class MsgException(exceptionMessage: String) extends AppMsg

class UsersRoute(userRepo: UserRepository)(implicit ec: ExecutionContext){
  import UsersRoute.Responsable._
  val route = path("users" / Segment) {
    username => {
      get {
        complete(UserFuture.toResponse(userRepo.getUserByUsername(username)))
      } ~ delete {
        complete(ResultSetFuture.toResponse(userRepo.deleteByUsername(username))) 
      }
    }
  } ~ path("users"){
    post {
      entity(as[User]) { user =>
        complete(ResultSetFuture.toResponse(userRepo.saveOrUpdate(user)))
      }
    } ~ put {
      entity(as[User]){ user =>
        complete(ResultSetFuture.toResponse(userRepo.saveOrUpdate(user)))
      }
    } ~ get {
      complete(SeqUserFuture.toResponse(userRepo.getUsers))
    }
  }
}

object UsersRoute {
  type FutureResponse = Future[(StatusCode, Option[AppMsg])]
  trait Responsable[T] {
    def toResponse(fut: Future[T])(implicit ec: ExecutionContext): FutureResponse
  }
  object Responsable {
    import StatusCodes._
    object ResultSetFuture extends Responsable[ResultSet] {
      def toResponse(fut: Future[ResultSet])(implicit ec: ExecutionContext): FutureResponse = {
        fut.map(res => (OK, None)).recover{
          case ex => (InternalServerError, Some(MsgException(ex.getMessage)))
        }
      }
    }
    object UserFuture extends Responsable[Option[User]]{
      def toResponse(fut: Future[Option[User]])(implicit ec: ExecutionContext): FutureResponse = {
        fut.map(x => {
          if(x.isDefined) (OK, Some(MsgUser(x.get)))
          else (NotFound, None)
        }).recover{
          case ex => (InternalServerError, Some(MsgException(ex.getMessage)))
        }
      }
    }
    object SeqUserFuture extends Responsable[Seq[User]] {
      def toResponse(fut: Future[Seq[User]])(implicit ec: ExecutionContext): FutureResponse = {
        fut.map(x => (OK, Some(MsgUsers(x)))).recover {
          case ex => (InternalServerError, Some(MsgException(ex.getMessage)))
        }
      }
    }
  }
}
