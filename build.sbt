

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

lazy val buildNg2App = taskKey[Unit]("build ng2 application")

buildNg2App := {
  val rootPath: File = baseDirectory.value / "public"
  val nodeModules: File = baseDirectory.value / "public" / "node_modules"
  if (!nodeModules.exists())
     Process("npm" :: "install" :: Nil, rootPath).!
  Process("ng" :: "build" :: Nil, rootPath).!
}

//(compile in Compile) <<= (compile in Compile) dependsOn buildNg2App

(stage in Universal) <<= (stage in Universal) dependsOn buildNg2App

watchSources := watchSources.value.filterNot { x => BuildUtils.isParentFile(baseDirectory.value / "public" / "dist", x) }

cleanFiles <+= baseDirectory { base => base / "public" / "dist" }




