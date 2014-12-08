
name          := "Truss Webapp"

version       := "0.1"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", 
                     "-deprecation", 
                     "-encoding", "utf8", 
                     "-feature", 
                     "-language:implicitConversions")

resolvers += "Twitter" at "http://maven.twttr.com"

libraryDependencies ++= Seq(
    "com.twitter" %% "finatra" % "1.5.3",
    "com.google.code.gson" %  "gson" % "2.2.4"
)
