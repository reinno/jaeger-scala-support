
lazy val global = project
  .in(file("."))
  .settings(name := "jaeger-scala-client", settings)
  .aggregate(
    client,
    `akka-http-client`,
    `akka-http-example`,
    `spray-client`,
    `spray-example`)

val `scalaVersion2.11` = "2.11.8"
val `scalaVersion2.12` = "2.12.4"
lazy val client = project
  .settings(
    version := Dependencies.clientV,
    name := "akka-jaeger-client",
    crossScalaVersions := Seq(`scalaVersion2.11`, `scalaVersion2.12`),
    settings,
    libraryDependencies ++= Dependencies.commonDependencies
  )

lazy val `akka-http-client` = project
  .settings(
    version := Dependencies.clientV,
    name := "akka-http-jaeger-client",
    crossScalaVersions := Seq(`scalaVersion2.11`, `scalaVersion2.12`),
    settings,
    libraryDependencies ++= Dependencies.akkaHttpClientDependencies
  )

lazy val `akka-http-example` = project
  .settings(
    version := "0.1",
    name := "akka-http-example",
    mainClass in assembly := Some("io.github.reinno.Boot"),
    scalaVersion := `scalaVersion2.12`,
    settings,
    libraryDependencies ++= Dependencies.akkaHttpExampleDependencies
  )

lazy val `spray-client` = project
  .settings(
    version := Dependencies.clientV,
    name := "spray-jaeger-client",
    scalaVersion := `scalaVersion2.11`,
    settings,
    libraryDependencies ++= Dependencies.sprayClientDependencies
  )

lazy val `spray-example` = project
  .settings(
    version := "0.1",
    name := "spray-example",
    mainClass in assembly := Some("io.github.reinno.Boot"),
    scalaVersion := `scalaVersion2.11`,
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
  organization := "io.github.reinno"
))

lazy val settings = commonSettings
