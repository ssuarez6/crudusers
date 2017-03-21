package co.s4n.httptests

import co.s4n.httptests.utils.DatabaseTest
import co.s4n.users.persistance.User
import org.scalatest.WordSpec
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

class UserDBTest extends WordSpec with DatabaseTest{
  "The database handler" should {
    "store a user and retrieve it " in {
      val user = User(UUID.randomUUID(), "test_name", "full name test", 20)
      val chain = for {
        store <- database.userModel.store(user)
        get <- database.userModel.getUserByUsername(user.username)
      } yield get
      whenReady(chain) {
        res =>
          res shouldBe defined
          res.value shouldEqual user
      }
    }

    "delete an user and not find it after deletion" in {
      val user = User(UUID.randomUUID(), "test_name", "full name test", 21)
      val chain = for {
        store <- database.userModel.store(user)
        delete <- database.userModel.deleteUserByUsername(user.username)
        get <- database.userModel.getUserByUsername(user.username)
      } yield get
      whenReady(chain){
        res =>
          res should not be defined
      }
    }

    "get all users in a list" in {
      val user = User(UUID.randomUUID(), "test_name1", "full name test", 21)
      val user2 = User(UUID.randomUUID(), "test_name2", "full name test", 20)
      val chain = for{
        _ <- database.userModel.store(user)
        _ <- database.userModel.store(user2)
        getAll <- database.userModel.getUsers
      } yield getAll
      whenReady(chain) {
        res =>
          res.size == 2
      }
    }
  }
}
