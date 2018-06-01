
lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    client,
    akkahttpexample
  )

lazy val client = project
  .settings(
    version := "0.1",
    inThisBuild(List(
      organization    := "io.github.reinno",
      scalaVersion    := "2.11.6"
    )),
    name := "akka-jaeger-client",
    settings,
    libraryDependencies ++= Dependencies.commonDependencies
  )

lazy val akkahttpexample = project
  .settings(
    version := "0.1",
    inThisBuild(List(
      organization    := "io.github.reinno",
      scalaVersion    := "2.11.6"
    )),
    name := "akka-http-jaeger-example",
    settings,
    libraryDependencies ++= Dependencies.akkahttpExampleDependencies
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
)

lazy val settings = commonSettings