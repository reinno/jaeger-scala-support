import sbt._
import Keys._

object Dependencies {
  lazy val akkaHttpVersion = "10.1.1"
  lazy val akkaVersion = "2.5.12"
  lazy val jaegerVersion = "0.28.0"
  lazy val sprayServerV = "1.3.1"
  lazy val scalaTestV = "3.0.1"

  val commonDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

    "io.spray"          %% "spray-can"            % sprayServerV,
    "io.spray"          %% "spray-routing-shapeless2" % sprayServerV,

    "io.jaegertracing"  %  "jaeger-core"          % jaegerVersion,
    "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
    "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
    "org.scalatest"     %% "scalatest"            % scalaTestV      % Test
  )

  val clientDependencies: Seq[ModuleID] = Seq (
    "io.github.reinno"  %% "akka-jaeger-client"   % "0.1"
  )

  val akkahttpExampleDependencies: Seq[ModuleID] = commonDependencies ++ clientDependencies
  val sprayExampleDependencies: Seq[ModuleID] = commonDependencies ++ clientDependencies
}
