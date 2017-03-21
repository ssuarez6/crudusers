package co.s4n.httptests.utils

import akka.actor.ActorSystem
import co.s4n.users.persistance.EmbeddedDatabase
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor

trait DatabaseTest extends Suite
  with Matchers
  with Inspectors
  with ScalaFutures
  with OptionValues
  with BeforeAndAfterAll
  with EmbeddedDatabase{
  override def beforeAll(): Unit = {
    super.beforeAll()
    implicit val system = ActorSystem("test")
    implicit val executor: ExecutionContextExecutor = system.dispatcher
    database.create(10 seconds)
  }
}
