package co.s4n.users.persistance

import com.outworkers.phantom.dsl._
import scala.concurrent.Future
import java.util.UUID

class UserModel extends CassandraTable[ConcreteUserModel, User] {
  override def tableName: String = "users"

  object id extends UUIDColumn(this) {
    override lazy val name = "user_id"
  }

  object username extends StringColumn(this) with PartitionKey
  
  object fullName extends StringColumn(this)

  object age extends IntColumn(this)

  override def fromRow(r: Row): User = User(id(r), username(r), fullName(r), age(r))
}

abstract class ConcreteUserModel extends UserModel with RootConnector {
  def getUserByUsername(username: String): Future[Option[User]] = {
    select
      .where(_.username eqs username)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }

  def store(user: User): Future[User] = {
    insert
      .value(_.id, user.id)
      .value(_.username, user.username)
      .value(_.fullName, user.fullName)
      .value(_.age, user.age)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
      .map(_ => user)
  }

  def deleteUserByUsername(username: String): Future[String] = {
    val query = delete
      .where(_.username eqs username)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
    query.map(_ => username)
  }

  def getUsers: Future[Seq[User]] = {
    select
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .fetch()
  }
}
