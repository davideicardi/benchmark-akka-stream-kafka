name := "benchmark-akka-stream-kafka"

version := "0.1"

scalaVersion := "2.12.8"


val akkaVersion = "2.5.22"
val akkaStreamKafkaVersion = "1.0.3"
val typesafeConfigVersion = "1.3.3"

libraryDependencies += "com.typesafe" % "config" % typesafeConfigVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % akkaStreamKafkaVersion
