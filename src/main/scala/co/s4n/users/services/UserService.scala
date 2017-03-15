package co.s4n.users.services

import co.s4n.users.persistance.ProductionDatabase
import co.s4n.users.persistance.User
import com.outworkers.phantom.dsl._
import scala.concurrent.Future

trait UserService extends ProductionDatabase {
  def getUserById(id: Int): Future[Option[User]] = 
    database.userModel.getUserById(id)

  def saveOrUpdate(user: User): Future[ResultSet] =
    database.userModel.store(user)

  def deleteById(id: Int): Future[ResultSet] =
    database.userModel.deleteUserById(id)

  def getUsers: Future[Seq[User]] =
    database.userModel.getUsers
}

object UserService extends UserService with ProductionDatabase
