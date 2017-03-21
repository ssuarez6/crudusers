package co.s4n.users.services

import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.model.{StatusCodes, StatusCode}
import co.s4n.users.persistance.User
import StatusCodes._

sealed trait AppMsg
case class MsgUser(user: User) extends AppMsg
case class MsgUsers(users: Seq[User]) extends AppMsg
case class MsgException(exceptionMessage: String) extends AppMsg

trait Responsable[T] {
  type FutureResponse = Future[(StatusCode, Option[AppMsg])]
  def toResponse(fut: Future[T])(implicit ec: ExecutionContext): FutureResponse
}
object Responsable {
  implicit object StringResponsable extends Responsable[String] {
    def toResponse(fut: Future[String])(implicit ec: ExecutionContext): FutureResponse = {
      fut.map(res => (OK, None)).recover{
        case ex => (InternalServerError, Some(MsgException(ex.getMessage)))
      }
    }
  }

  implicit object UserResponsable extends Responsable[User] {
    def toResponse(fut: Future[User])(implicit ec: ExecutionContext): FutureResponse = {
      fut.map(x =>{
        (OK, Some(MsgUser(x)))
        }).recover {
          case ex => (InternalServerError, Some(MsgException(ex.getMessage)))
        }
    }
  }
  implicit object UserOptionResponsable extends Responsable[Option[User]]{
    def toResponse(fut: Future[Option[User]])(implicit ec: ExecutionContext): FutureResponse = {
      fut.map(x => {
        if(x.isDefined) (OK, Some(MsgUser(x.get)))
        else (NotFound, None)
        }).recover{
          case ex => (InternalServerError, Some(MsgException(ex.getMessage)))
        }
    }
  }
  implicit object SeqUserResponsable extends Responsable[Seq[User]] {
    def toResponse(fut: Future[Seq[User]])(implicit ec: ExecutionContext): FutureResponse = {
      fut.map(x => (OK, Some(MsgUsers(x)))).recover {
        case ex => (InternalServerError, Some(MsgException(ex.getMessage)))
      }
    }
  }
}
