package co.s4n.crudusers 

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, ContentTypes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.util.Random
import scala.io.StdIn

object WebServer {
  def main(args: Array[String]) = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val numbers = Source.fromIterator(() => 
        Iterator.continually(Random.nextInt()))

    val route = 
      get{
        path("random"){
          complete(
            HttpEntity(
              ContentTypes.`text/plain(UTF-8)`,
              numbers.map(n => ByteString(s"$n\n"))
            )
          )
        }
      }

    val bindingFigure = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at port 8080, press RETURN to stop it")
    StdIn.readLine
    bindingFigure.flatMap(_.unbind).onComplete(_ => system.terminate)
  }
}
