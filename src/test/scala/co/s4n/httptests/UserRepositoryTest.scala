package co.s4n.httptests

import co.s4n.users.persistance.{User, UserRepository}
import java.util.UUID
import scala.concurrent.{Future, ExecutionContext}
import com.outworkers.phantom.dsl._

object UserRepositoryTest extends UserRepository {
  val users: Seq[User] = 
    Seq[User](User(UUID.randomUUID, "usertest", "testname", 24),
      User(UUID.randomUUID, "usertest2", "testname", 24),
      User(UUID.randomUUID, "usertest3", "testname", 24),
      User(UUID.randomUUID, "usertest4", "testname", 24)
    )
  override def getUserByUsername(username:String): Future[Option[User]] = {
    Future{
      users.find(x => x.username == username)
    }
  }

  override def getUsers: Future[Seq[User]] = {
    Future(users)
  }

  override def deleteByUsername(username: String): Future[String] = {
    Future(username)
  }

  override def saveOrUpdate(user: User): Future[User] = {
    Future(user)
  }
}
