lazy val akkaHttpVersion = "10.1.1"
lazy val akkaVersion    = "2.5.12"
lazy val jaegerVersion = "0.28.0"
lazy val sprayServerV = "1.3.1"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "io.github.reinno",
      scalaVersion    := "2.11.6"
    )),
    name := "akka-jaeger-client",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "io.jaegertracing"  %  "jaeger-core"          % jaegerVersion,

      "io.spray"          %% "spray-can"            % sprayServerV,
      "io.spray"          %% "spray-routing-shapeless2" %   sprayServerV,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
    )
  )
