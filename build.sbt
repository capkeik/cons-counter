ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11-M2"

lazy val root = (project in file("."))
  .settings(
    name := "cost-accounting",
    scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
    libraryDependencies ++= Seq(
      "io.estatico" %% "newtype" % "0.4.4",
      "eu.timepit" %% "refined" % "0.10.3",
      "eu.timepit" %% "refined-cats" % "0.10.3",
      "org.typelevel" %% "cats-effect" % "3.5.0",
      "tf.tofu" %% "derevo-cats" % "0.13.0",
      "tf.tofu" %% "tofu-logging" % "0.12.0.1",
      "tf.tofu" %% "tofu-logging-derivation" % "0.12.0.1",
      "tf.tofu" %% "tofu-logging-layout" % "0.12.0.1",
      "tf.tofu" %% "tofu-logging-logstash-logback" % "0.12.0.1",
      "tf.tofu" %% "tofu-logging-structured" % "0.12.0.1",
      "tf.tofu" %% "tofu-core-ce3" % "0.12.0.1",
      "tf.tofu" %% "tofu-doobie-logging-ce3" % "0.12.0.1",
      "tf.tofu" %% "derevo-circe" % "0.13.0",
      "com.github.jwt-scala" %% "jwt-core" % "9.3.0",
      "dev.profunktor" %% "redis4cats-effects" % "1.4.1"
    ),
    dependencyOverrides += "io.circe" %% "circe-core" % "0.14.3"
  )
