package co.s4n.users.services

import java.util.Properties

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import co.s4n.users.persistance.{User, UserRepository}
import scala.concurrent.{ExecutionContext, Future}
import Responsable._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class UsersRoute(userRepo: UserRepository)(implicit ec: ExecutionContext){
  def handle[T](arg: Future[T])(implicit resp: Responsable[T]) = {
    resp.toResponse(arg)
  }

  val route = path("users" / Segment) {
    username => {
      get {
        complete(handle(userRepo.getUserByUsername(username)))
        } ~ delete {
          complete(handle(userRepo.deleteByUsername(username))) 
        }
    }
  } ~ path("users"){
    post {
      entity(as[User]) { user =>
        val props = new Properties()
        props.put("bootstrap.servers", "127.0.0.1:9092")
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("auto.commit.interval.ms", "1000")
        val producer = new KafkaProducer[String, String](props)
        producer.send(new ProducerRecord[String, String]("users-creation", user.toString))
        complete(handle(userRepo.saveOrUpdate(user)))
      }
      } ~ put {
        entity(as[User]){ user =>
          complete(handle(userRepo.saveOrUpdate(user)))
        }
        } ~ get {
          complete(handle(userRepo.getUsers))
        }
  } 
}
