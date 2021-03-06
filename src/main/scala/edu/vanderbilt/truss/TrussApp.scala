package edu.vanderbilt.truss

import java.io.File
import scala.util.Properties

import akka.actor.{OneForOneStrategy, Props, ActorSystem}
import akka.io.{Inet, IO}
import akka.dispatch.sysmsg.Supervise
import spray.can.Http


object TrussApp extends App with LegacyTest {

  println("===============================================================")
  println("|                         Truss Webapp                        |")
  println("===============================================================")
  println()
  
  this.args match {
    case Array("help")        => printGuide()
    case Array(address, port) => bootServer(address, port.toInt)
    case Array(address)       => bootServer(address, 8080)
    case _                    => bootServer("localhost",
                                             Properties.envOrElse("PORT", "8080").toInt)
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

    println(s"Starting truss server at: $ipAddress:$portNumber/")

    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("truss-webapp")

    // create and start our service actor
    val service = system.actorOf(Props[MainServer], "server")

    // start a new HTTP server on port 8080 with our service actor as the handler
    IO(Http) ! Http.Bind(listener = service,
                          interface = ipAddress,
                          port = portNumber)

    println("Hit any key to exit.")
    val result = readLine()
    system.shutdown()
  }

  def openInputSet = new File(getClass.getResource("/sample_input_set.txt").getPath)

  def ERROR = "| ERROR | "

}


