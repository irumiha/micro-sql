name := "microSQL"

version := "1.0"

organization := "com.rumi"

scalaVersion := "2.9.2"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.3.155" % "test",
  "postgresql" % "postgresql" % "9.0-801.jdbc4" % "test",
  "org.scalatest" %% "scalatest" % "1.7.2" % "test"
)

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
                  "releases" at "http://scala-tools.org/repo-releases")

