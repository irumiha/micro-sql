name := "microSQL"

version := "1.0"

organization := "com.rumi"

scalaVersion := "2.8.1"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.3.155" % "test",
  "org.scalatest" %% "scalatest" % "1.5.1"
)

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
                  "releases" at "http://scala-tools.org/repo-releases")

