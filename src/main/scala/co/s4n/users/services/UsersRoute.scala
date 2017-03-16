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

class UsersRoute(implicit ec: ExecutionContext){
  import UsersRoute.Responsable._
  val route = path("users" / Segment) {
    username => {
      get {
        complete(UserFuture.toResponse(UserDatabase.getUserByUsername(username)))
      } ~ delete {
        complete(ResultSetFuture.toResponse(UserDatabase.deleteByUsername(username))) 
      }
    }
  } ~ path("users"){
    post {
      entity(as[User]) { user =>
        complete(ResultSetFuture.toResponse(UserDatabase.saveOrUpdate(user)))
      }
    } ~ put {
      entity(as[User]){ user =>
        complete(ResultSetFuture.toResponse(UserDatabase.saveOrUpdate(user)))
      }
    } ~ get {
      complete(SeqUserFuture.toResponse(UserDatabase.getUsers))
    }
  }
}

object UsersRoute {
  sealed trait AppMsg
  case class MsgUser(user: User) extends AppMsg
  case class MsgUsers(users: Seq[User]) extends AppMsg
  case class MsgException(exceptionMessage: String) extends AppMsg

  trait Responsable[T] {
    def toResponse(fut: Future[T])(implicit ec: ExecutionContext): Future[(StatusCode, Option[AppMsg])]
  }
  object Responsable {
    object ResultSetFuture extends Responsable[ResultSet] {
      def toResponse(fut: Future[ResultSet])(implicit ec: ExecutionContext): Future[(StatusCode, Option[AppMsg])] = {
        fut.map(res => (StatusCodes.OK, None)).recover{
          case ex => (StatusCodes.InternalServerError, Some(MsgException(ex.getMessage)))
        }
      }
    }

    object UserFuture extends Responsable[Option[User]]{
      def toResponse(fut: Future[Option[User]])(implicit ec: ExecutionContext): Future[(StatusCode, Option[AppMsg])] = {
        fut.map(x => {
          if(x.isDefined) (StatusCodes.OK, Some(MsgUser(x.get)))
          else (StatusCodes.NotFound, None)
          }).recover{
            case ex => (StatusCodes.InternalServerError, Some(MsgException(ex.getMessage)))
          }
      }
    }

    object SeqUserFuture extends Responsable[Seq[User]] {
      def toResponse(fut: Future[Seq[User]])(implicit ec: ExecutionContext): Future[(StatusCode, Option[AppMsg])] = {
        fut.map(x => {
          (StatusCodes.OK, Some(MsgUsers(x)))
          }).recover {
            case ex => (StatusCodes.InternalServerError, Some(MsgException(ex.getMessage)))
          }
      }
    }
  }
}
