package co.s4n.users.services

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer

class MessageConsumer extends Runnable {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("group.id", "usersconsumer")
  props.put("enable.auto.commit", "false")
  props.put("session.timeout.ms", "10000")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  override def run() = {
    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Arrays.asList("users-creation"))
    while(true){
      val records = consumer.poll(200)
      val it = records.iterator()
      while(it.hasNext){
        val record = it.next()
        println(s"Got a message of topic ${record.topic()}")
        println(s"Message: ${record.value()}")
      }
    }
  }
}