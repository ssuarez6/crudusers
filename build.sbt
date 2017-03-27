name := "Tests"

version := "1.0"

scalaVersion := "2.12.1"

resolvers ++= Seq(
  Resolver.bintrayRepo("hseeberger", "maven"),
  "Typesafe repository releases" at "http://repo.typesafe" + ".com/typesafe/releases/",
  "Twitter Repository"           at "http://maven.twttr.com",
  Resolver.bintrayRepo("cakesolutions", "maven")
)

libraryDependencies ++= {
  val akkaV = "10.0.4"
  val phantomV = "2.3.1"
  val circeV = "0.7.0"

  Seq(
    "org.scalatest"       %% "scalatest"            % "3.0.1" % "test",
    "org.scalactic"       %% "scalactic"            % "3.0.1",
    "com.typesafe.akka"   %% "akka-http"            % akkaV,
    "com.typesafe.akka"   %% "akka-http-spray-json" % akkaV,
    "com.typesafe.akka"   %% "akka-http-testkit"    % akkaV,
    "com.outworkers"      %% "phantom-dsl"          % phantomV,
    "io.circe"            %% "circe-core"           % circeV,
    "io.circe"            %% "circe-generic"        % circeV,
    "io.circe"            %% "circe-parser"         % circeV,
    "de.heikoseeberger"   %% "akka-http-circe"      % "1.13.0",
    "com.typesafe.akka"   %% "akka-stream-kafka"    % "0.14",
    "org.apache.kafka"    %%  "kafka"               % "0.10.2.0" exclude("org.slf4j", "slf4j-log4j12")
  )
}


cancelable in Global := true 

PhantomSbtPlugin.projectSettings

com.updateimpact.Plugin.apiKey in ThisBuild := "373nusHCPn5K9a40JMtjHcDANcMU8TO9"
