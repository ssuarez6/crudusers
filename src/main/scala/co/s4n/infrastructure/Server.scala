package co.s4n.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.io.StdIn

object UsersServer extends App{
  implicit val system = ActorSystem("crud-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val up = new co.s4n.users.persistance.UsersPersistance()
  val ur = new co.s4n.users.services.UsersRoute(up)

  val bindingFuture = Http().bindAndHandle(ur.route, "localhost", 8080)
  println("Server online at localhost:8080\nRETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate)
}
