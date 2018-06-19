
lazy val global = project
  .in(file("."))
  .settings(name := "akka-jaeger", settings)
  .aggregate(client, akkahttpexample, sprayexample)

lazy val client = project
  .settings(
    version := "0.1",
    name := "akka-jaeger-client",
    settings,
    libraryDependencies ++= Dependencies.commonDependencies
  )

lazy val akkahttpexample = project
  .settings(
    version := "0.1",
    name := "akka-jaeger-akka-http-example",
    mainClass in assembly := Some("io.github.reinno.Boot"),
    settings,
    libraryDependencies ++= Dependencies.akkahttpExampleDependencies
  )

lazy val sprayexample = project
  .settings(
    version := "0.1",
    name := "akka-jaeger-spray-example",
    mainClass in assembly := Some("io.github.reinno.Boot"),
    settings,
    libraryDependencies ++= Dependencies.sprayExampleDependencies
  )

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
) ++ inThisBuild(List(
  organization := "io.github.reinno",
  scalaVersion := "2.11.8"
))

lazy val settings = commonSettings
