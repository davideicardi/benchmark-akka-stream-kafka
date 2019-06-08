name := "benchmark-akka-stream-kafka"

version := "0.2"

scalaVersion := "2.12.8"


val akkaVersion = "2.5.22"
val akkaStreamKafkaVersion = "1.0.3"
val typesafeConfigVersion = "1.3.3"
val log4jVersion = "2.11.1"
val slf4jVersion = "1.7.25"

libraryDependencies += "com.typesafe" % "config" % typesafeConfigVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % akkaStreamKafkaVersion
libraryDependencies += "org.slf4j" % "slf4j-api" % slf4jVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % log4jVersion
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % log4jVersion


enablePlugins(JavaServerAppPackaging)

trapExit := false
topLevelDirectory := None
// Prepend a conf and plugins directory to the classpath to allow to customize settings
scriptClasspath ~= (cp => "../conf"  +: cp)
