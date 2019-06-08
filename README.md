# benchmark-akka-stream-kafka

Simple console app to benchmark akka stream kafka


## Create deploy package

    sbt clean universal:packageBin

## Installation

Get the package:

    curl -O -L https://github.com/davideicardi/benchmark-akka-stream-kafka/releases/download/v0.2/benchmark-akka-stream-kafka-0.2.zip
    unzip benchmark-akka-stream-kafka-0.2.zip

IMPORTANT: create a configuration inside `./conf/application.conf` file with your specific configuration:

```
sampleTopic = "topicSample"
recordBodyFile = "./recordBodyFile.bin"

akka.kafka.producer {
  # Properties defined by org.apache.kafka.clients.producer.ProducerConfig
  # can be defined in this configuration section.
  #  https://kafka.apache.org/documentation/#producerconfigs
  kafka-clients {
    bootstrap.servers = "localhost:9092"
    client.id = "sample-producer-1"

    # Enable kerberos auth
    # security.protocol = "SASL_SSL"
    # sasl.kerberos.service.name = "kafka"
  }
}
```

Run the benchmark:

    ./bin/benchmark-akka-stream-kafka