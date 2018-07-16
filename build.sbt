name := "microSQL"

version := "1.0"

organization := "com.rumi"

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.10.5", "2.11.6", "2.12.6")

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.197",
  "org.postgresql" % "postgresql" % "42.2.4",
  "junit" % "junit" % "4.8" % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 12 =>
      libraryDependencies.value :+ "org.scala-lang" % "scala-reflect" % "2.12.6"
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
