
name := """valkyrie"""
organization := "com.github.rogue-one"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.github.rogue-one.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.github.rogue-one.binders._"

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.typesafe.play"   %%   "play-slick"              %   "2.0.0",
  "com.typesafe.play"   %%   "play-slick-evolutions"   %   "2.0.0",
  "com.h2database"    % 	   "h2"                    %   "1.4.187" ,
  "mysql" % "mysql-connector-java" % "6.0.5",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "org.quartz-scheduler" % "quartz" % "2.3.0"
)

javaOptions in Test += "-Dconfig.file=conf/application-test.conf"
