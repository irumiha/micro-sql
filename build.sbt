name := "microSQL"

version := "1.0"

organization := "com.rumi"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.10.5", "2.11.6")

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.3.155",
  "postgresql" % "postgresql" % "9.0-801.jdbc4",
  "junit" % "junit" % "4.8" % "test",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test"
)

libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value :+ "org.scala-lang" % "scala-reflect" % "2.11.6"
    case _ =>
      libraryDependencies.value :+ "org.scala-lang" % "scala-reflect" % "2.10.5"
  }
}

resolvers ++= Seq(
    "snapshots" at "http://scala-tools.org/repo-snapshots",
    "releases" at "http://scala-tools.org/repo-releases",
    "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
    "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
