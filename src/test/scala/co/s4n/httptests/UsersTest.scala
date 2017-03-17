package co.s4n.httptests

import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import co.s4n.users.services.UsersRoute
import co.s4n.users.persistance.ProductionDatabase
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.FlatSpec

class UserTest extends WordSpec with Matchers with ScalatestRouteTest with ProductionDatabase {
  implicit val actorSystem = ActorSystem("crud-system")
  implicit val executionContext = system.dispatcher
  val route = new UsersRoute().route
  database.create(5 seconds)

  "The service" should {
    "handle a GET request to the users path" in {
      Get("/users") ~> route ~> check{
        handled shouldBe true
      }
    }
  }
}
