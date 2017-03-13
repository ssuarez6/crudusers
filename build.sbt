name := "Tests"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.4"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % "1.13.0"
libraryDependencies += "io.circe" %% "circe-core" % "0.7.0"
libraryDependencies += "io.circe" %% "circe-generic" % "0.7.0"
libraryDependencies += "io.circe" %% "circe-parser" % "0.7.0"

cancelable in Global := true //permite ctrl-c sin salir de sbt
