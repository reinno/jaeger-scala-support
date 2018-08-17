import sbt._
import Keys._

object Dependencies {
  lazy val akkaHttpVersion = "10.1.1"
  lazy val akkaVersion = "2.5.12"
  lazy val jaegerVersion = "0.28.0"
  lazy val sprayVersion = "1.3.1"
  lazy val json4sVersion = "3.5.4"
  lazy val scalaTestV = "3.0.1"
  lazy val clientV = "0.1.4"

  val commonDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

    "io.spray"          %% "spray-can"            % sprayVersion,
    "io.spray"          %% "spray-routing-shapeless2" % sprayVersion,
    "org.json4s"        %% "json4s-native"        % json4sVersion,
    "io.jaegertracing"  %  "jaeger-core"          % jaegerVersion,

    "org.scalatest"     %% "scalatest"            % scalaTestV      % Test
  )

  val clientDependencies: Seq[ModuleID] = Seq (
    "io.github.reinno"  %% "akka-jaeger-client"   % clientV
  )

  val akkahttpExampleDependencies: Seq[ModuleID] = commonDependencies ++ clientDependencies ++ Seq (
    "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
    "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test
  )

  val sprayExampleDependencies: Seq[ModuleID] = commonDependencies ++ clientDependencies ++ Seq (
    //"io.spray"          %% "spray-routing"        % sprayVersion,
    "io.spray"          %% "spray-httpx"          % sprayVersion,
    "io.spray"          %% "spray-testkit"        % sprayVersion     % Test
  )
}
