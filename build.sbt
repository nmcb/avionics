name := "avionics"

organization := "splatter"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.1"

scalacOptions += "-deprecation"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies += "org.scalatest" %% "scalatest" % "2.0.M6-SNAP16" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.1.2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1.2"