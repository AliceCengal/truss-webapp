organization  := "edu.vanderbilt.truss"

version       := "0.1"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

name          := "Truss Webapp"

libraryDependencies ++= Seq(
    "com.google.code.gson" % "gson" % "2.2.4"
)
