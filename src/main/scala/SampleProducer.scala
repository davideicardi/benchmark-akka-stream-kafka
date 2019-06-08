import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import org.apache.kafka.common.serialization.ByteArraySerializer

object SampleProducer {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("SampleProducer")

    val config = system.settings.config.getConfig("akka.kafka.producer")
    val producerSettings = ProducerSettings(config, new ByteArraySerializer, new ByteArraySerializer)
        .withBootstrapServers(bootstrapServers)
  }
}
