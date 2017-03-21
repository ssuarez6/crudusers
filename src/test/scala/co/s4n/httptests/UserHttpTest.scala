package co.s4n.httptests

import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import co.s4n.users.services.UsersRoute

class UserHttpTest extends WordSpec with Matchers with ScalatestRouteTest {
  implicit val executionContext = system.dispatcher
  val route = new UsersRoute(UserRepositoryTest).route

  "The service" should {
    "handled a GET request to the users path" in {
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

    "unhandled invalid paths" in {
      Get("/invalidpath") ~> route ~> check {
        handled shouldBe false
      }
    }

    "not find users that don't exist" in {
      Get("/users/nonexistinguser") ~> route ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "allow deletion on an specific user" in {
      Delete("/users") ~> route ~> check {
        handled shouldBe false
      }

      Delete("/users/some") ~> route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "allow POST only at /users path" in {
      Post("/different") ~> route ~> check {
        handled shouldBe false
      }
      
      val json = """
      {
        "id": "375b705f-d74d-49c4-8096-b21ecddc1cfc",
        "username": "test",
        "fullName": "lasdkjfañlds",
        "age": 20
      }
      """

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/users",
        entity = HttpEntity(MediaTypes.`application/json`, json)
      )

      postRequest ~> route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
      }
    }

    "allow PUT only at /users" in {

      val json = """
      {
        "id": "375b705f-d74d-49c4-8096-b21ecddc1cfc",
        "username": "test",
        "fullName": "lasdkjfañlds",
        "age": 25
      }
      """

      val putReq = HttpRequest(
        HttpMethods.PUT,
        uri = "/users",
        entity = HttpEntity(MediaTypes.`application/json`, json)
      )

      putReq ~> route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
      }

      val invalid = HttpRequest(
        HttpMethods.PUT,
        uri = "/asdlkjf",
        entity = HttpEntity(MediaTypes.`application/json`, json)
      )

      invalid ~> route ~> check {
        handled shouldBe false
      }
    }
  }

  system.terminate
}
