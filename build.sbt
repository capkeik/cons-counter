ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11-M2"

lazy val root = (project in file("."))
  .settings(
    name := "cost-accounting"
  )
