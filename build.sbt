name := "Tests"

version := "1.0"

scalaVersion := "2.12.1"

resolvers ++= Seq(
  Resolver.bintrayRepo("hseeberger", "maven"),
  "Typesafe repository releases" at "http://repo.typesafe" + ".com/typesafe/releases/",
  "Twitter Repository"           at "http://maven.twttr.com"
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
    "de.heikoseeberger"   %% "akka-http-circe"      % "1.13.0"
  )
}

cancelable in Global := true 
Revolver.settings

//PhantomSbtPlugin.projectSettings
