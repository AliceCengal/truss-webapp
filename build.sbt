organization  := "edu.vanderbilt.truss"

version       := "0.1"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

name          := "Truss Webapp"

resolvers ++= Seq(
    "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
    "io.spray"            %   "spray-can"     % "1.2.0",
    "io.spray"            %   "spray-routing" % "1.2.0",
    "io.spray"            %   "spray-testkit" % "1.2.0",
    "com.typesafe.akka"   %%  "akka-actor"    % "2.2.3",
    "com.typesafe.akka"   %%  "akka-testkit"  % "2.2.3",
    "com.google.code.gson" % "gson" % "2.2.4"
)
