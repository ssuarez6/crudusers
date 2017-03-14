package co.s4n.users.persistance.model

import com.outworkers.phantom.dsl._
import scala.concurrent.Future
import co.s4n.users.persistance.entity.User

class UserModel extends CassandraTable[ConcreteUserModel, User] {
  override def tableName: String = "users"

  object id extends IntColumn(this) with PartitionKey {
    override lazy val name = "user_id"
  }

  object username extends StringColumn(this)

  override def fromRow(r: Row): User = User(id(r), username(r))
}

abstract class ConcreteUserModel extends UserModel with RootConnector {
  def getUserById(id: Int): Future[Option[User]] = {
    select
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }

  def store(user: User): Future[ResultSet] = {
    insert
      .value(_.id, user.id)
      .value(_.username, user.username)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }

  def deleteUserById(id: Int): Future[ResultSet] = {
    delete
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}
