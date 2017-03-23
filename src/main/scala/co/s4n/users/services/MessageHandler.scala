package co.s4n.users.services

import java.util.concurrent.atomic.AtomicLong

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.scaladsl.Source
import akka.stream.ActorMaterializer
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

import scala.concurrent.Future

class MessageProducer {
  implicit val system = ActorSystem("kafka")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val settings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  def produceMessage(topic: String, msg: String): Future[Done] = {
    Source.single(msg)
      .map(elem =>
          new ProducerRecord[Array[Byte], String](topic, msg))
            .runWith(Producer.plainSink(settings))
  }
}
class MessageConsumer {
  implicit val system = ActorSystem("kafka")
  implicit val materializer = ActorMaterializer()
  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val done = Consumer.committableSource(consumerSettings, Subscriptions.topics("users-creation"))
    .runForeach {
      msg => println(s"From consumer: ${msg.record.value}")
    }
}

object MessagePrinter extends App {
  (new MessageConsumer).done
}
