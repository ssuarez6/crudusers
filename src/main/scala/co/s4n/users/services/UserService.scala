package co.s4n.users.services

import co.s4n.users.persistance.ProductionDatabase
import co.s4n.users.persistance.User
import com.outworkers.phantom.dsl._
import scala.concurrent.Future

trait UserDatabase extends ProductionDatabase {
  def getUserByUsername(username: String): Future[Option[User]] = 
    database.userModel.getUserByUsername(username)

  def saveOrUpdate(user: User): Future[ResultSet] =
    database.userModel.store(user)

  def deleteByUsername(username: String): Future[ResultSet] =
    database.userModel.deleteUserByUsername(username)

  def getUsers: Future[Seq[User]] =
    database.userModel.getUsers
}

object UserDatabase extends UserDatabase with ProductionDatabase
