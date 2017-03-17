package co.s4n.users.persistance

import co.s4n.users.persistance.ProductionDatabase
import co.s4n.users.persistance.User
import com.outworkers.phantom.dsl._
import scala.concurrent.Future

trait UserRepository extends ProductionDatabase {
  def getUserByUsername(username: String): Future[Option[User]] = 
    database.userModel.getUserByUsername(username)

  def saveOrUpdate(user: User): Future[User] =
    database.userModel.store(user)

  def deleteByUsername(username: String): Future[String] =
    database.userModel.deleteUserByUsername(username)

  def getUsers: Future[Seq[User]] =
    database.userModel.getUsers
}

object UserRepository extends UserRepository with ProductionDatabase
