name := "Tests"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.4"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

cancelable in Global := true //permite ctrl-c sin salir de sbt