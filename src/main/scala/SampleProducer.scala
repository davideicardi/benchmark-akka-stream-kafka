import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.ProducerMessage.MultiMessage
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.{RecordHeader, RecordHeaders}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.Random

object SampleProducer {
  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("SampleProducer")
    implicit val contextExecutor: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    println("Benchmark Akka Stream Kafka - Sample Producer")

    val config = system.settings.config
    val topicName = config.getString("sampleTopic")
    val numberOfKeys = config.getInt("numberOfKeys")
    val keys = Seq.fill(numberOfKeys)(UUID.randomUUID().toString)
    val recordBody = Files.readAllBytes(Paths.get(config.getString("recordBodyFile")))
    val numberOfHeaders = 5

    val producerSettings = ProducerSettings(
      config.getConfig("akka.kafka.producer"),
      new StringSerializer,
      new ByteArraySerializer)

    Source.repeat(recordBody)
      .map { value =>

        val key = keys(Random.nextInt(keys.length))

        val headers = (1 to numberOfHeaders).map(i => new RecordHeader(s"sampleHeader-$i", s"$key$i".getBytes(StandardCharsets.UTF_8)))
        val recordHeaders = new RecordHeaders()
        headers.foreach(h => recordHeaders.add(h))

        MultiMessage[String, Array[Byte], Array[Byte]](
          Seq(
            new ProducerRecord(topicName, None.orNull, key, recordBody, recordHeaders)
          ),
          value
        )
      }
      .via(Producer.flexiFlow(producerSettings))
      .wireTapMat(printRate)(Keep.right)
      .log("SampleProducer")
      .runWith(Sink.ignore)

  }

  private def printRate[T] = {
    val interval = 1.seconds

    Flow[T]
      .conflateWithSeed(_ => 0) { case (acc, _) => acc + 1 }
      .zip(Source.tick(interval, interval, NotUsed))
      .map(_._1)
      .toMat(Sink.foreach(i => println(s"${i / interval.toSeconds.toFloat} received messages/second")))(Keep.right)
  }

}
