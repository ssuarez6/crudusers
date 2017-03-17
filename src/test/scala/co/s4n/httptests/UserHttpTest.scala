package co.s4n.httptests

import org.scalatest.{Matchers, WordSpec}
import org.scalatest.FlatSpec
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import co.s4n.users.services.UsersRoute
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer


class UserHttpTest extends WordSpec with Matchers with ScalatestRouteTest {
  implicit val executionContext = system.dispatcher
  val route = new UsersRoute(UserRepositoryTest).route

  "The service" should {
    "handle a GET request to the users path" in {
      Get("/users") ~> route ~> check{
        handled shouldBe true
      }
    }

    "get a user that exists on the system" in {
      Get("/users/usertest") ~> route ~> check {
        val resp = responseAs[String]
        resp.size >= 10
      }
    }

    "unhandle invalid paths" in {
      Get("/invalidpath") ~> route ~> check {
        handled shouldBe false
      }
    }

    "not find users that don't exist" in {
      Get("/users/nonexistinguser") ~> route ~> check {
        status == StatusCodes.NotFound
      }
    }

    "allow deletion on an specific user" in {
      Delete("/users") ~> route ~> check {
        status == StatusCodes.BadRequest
      }
    }
  }
}
