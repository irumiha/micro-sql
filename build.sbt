name := "microSQL"

version := "1.0"

organization := "com.rumi"

scalaVersion := "2.10.0"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

libraryDependencies ++= Seq(
  "com.chuusai" % "shapeless_2.10" % "1.2.3",
  "com.h2database" % "h2" % "1.3.155" % "test",
  "postgresql" % "postgresql" % "9.0-801.jdbc4" % "test",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "org.scala-lang" % "scala-reflect" % "2.10.0"
)

resolvers ++= Seq(
    "snapshots" at "http://scala-tools.org/repo-snapshots",
    "releases" at "http://scala-tools.org/repo-releases",
    "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
    "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

