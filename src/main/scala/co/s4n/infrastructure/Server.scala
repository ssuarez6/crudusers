package co.s4n.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.io.StdIn
import co.s4n.users.services.UsersRoute
import co.s4n.users.persistance.ProductionDatabase
import scala.concurrent.duration._

object UsersServer extends App with ProductionDatabase{
  implicit val system = ActorSystem("crud-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val ur = new UsersRoute
  database.create(5 seconds)

  val bindingFuture = Http().bindAndHandle(ur.route, "localhost", 8080)
  println("Server online at localhost:8080\nRETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate)
}
