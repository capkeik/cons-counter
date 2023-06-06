ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "cost-accounting",
    scalacOptions ++= List("-Ymacro-annotations"),
    libraryDependencies ++= Seq(
      "io.estatico" %% "newtype" % "0.4.4",
      "io.circe" %% "circe-core" % "0.15.0-M1",
      "io.circe" %% "circe-generic" % "0.15.0-M1",
      "io.circe" %% "circe-parser" % "0.15.0-M1",
      "io.circe" %% "circe-refined" % "0.15.0-M1",
      "eu.timepit" %% "refined" % "0.10.3",
      "eu.timepit" %% "refined-cats" % "0.10.3",
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.typelevel" %% "cats-effect" % "3.4.8",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC2",
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
    ),
    dependencyOverrides += "io.circe" %% "circe-core" % "0.14.5"
  )
