package edu.vanderbilt.truss

import java.io.File

import akka.actor.{Props, ActorSystem}
import akka.io.IO

import spray.can.Http
import com.typesafe.config.ConfigFactory

object TrussApp extends App with LegacyTest {

  println("===============================================================")
  println("|                         Truss Webapp                        |")
  println("===============================================================")
  println()

  this.args.toList match {
    case "help" :: _          => printGuide()
    case address :: port :: _ => bootServer(address, port.toInt)
    case address :: _         => bootServer(address, 8080)
    case _                    => bootServer("localhost", 8080)
  }

  def printGuide() {
    println(
             """usage:    java -jar TrussApp.jar [$IpAddress [$PortNumber]]
               |
               |$IpAddress :: IP Address of the server. Optional. Default to "localhost".
               |
               |$PortNumber :: Which port the server should listen to. Optional. Default
               |               to port 8080. For production, use port 80. Only root user
               |               can bind to port 80. Use sudo.
               |
               |example command:   sudo java -jar TrussApp.jar 129.44.33.22 80
               |
             """.stripMargin)
  }

  def bootServer(ipAddress: String, portNumber: Int) {

    println(s"Starting truss server at: http://$ipAddress:$portNumber/")

    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("truss-webapp", ConfigFactory.load(getClass.getClassLoader))

    // create and start our service actor
    val service = system.actorOf(Props[MainServer], "server")

    // start a new HTTP server on port 8080 with our service actor as the handler
    IO(Http) ! Http.Bind(service, interface = ipAddress, port = portNumber)

    println("Hit any key to exit.")
    val result = readLine()
    system.shutdown()
  }

  def openInputSet = new File(getClass.getResource("/sample_input_set.txt").getPath)

  def ERROR = "| ERROR | "

}


