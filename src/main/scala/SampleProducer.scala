import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util.UUID

import akka.actor.ActorSystem
import akka.kafka.ProducerMessage.MultiMessage
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.{RecordHeader, RecordHeaders}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

object SampleProducer {
  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("SampleProducer")
    implicit val contextExecutor: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val config = system.settings.config
    val topicName = config.getString("sampleTopic")
    val numberOfKeys = config.getInt("numberOfKeys")
    val keys = Seq.fill(numberOfKeys)(UUID.randomUUID().toString)
    val recordBody = Files.readAllBytes(Paths.get(config.getString("recordBodyFile")))

    val producerSettings = ProducerSettings(
      config.getConfig("akka.kafka.producer"),
      new StringSerializer,
      new ByteArraySerializer)

    Source.repeat(recordBody)
      .map { value =>

        val key = keys(Random.nextInt(keys.length))

        val sampleHeader1 = new RecordHeader("sampleHeader1", key.getBytes(StandardCharsets.UTF_8))
        val recordHeaders = new RecordHeaders()
        recordHeaders.add(sampleHeader1)

        MultiMessage[String, Array[Byte], Array[Byte]](
          Seq(
            new ProducerRecord(topicName, None.orNull, key, recordBody, recordHeaders)
          ),
          value
        )
      }
      .via(Producer.flexiFlow(producerSettings))
      .log("SampleProducer")
      .runWith(Sink.ignore)

  }
}
