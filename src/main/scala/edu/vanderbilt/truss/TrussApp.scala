package edu.vanderbilt.truss

import java.io.File

import akka.actor.{Props, ActorSystem}
import akka.io.IO

import spray.can.Http

object TrussApp extends App with LegacyTest {

  println("===============================================================")
  println("|                         Truss Webapp                        |")
  println("===============================================================")
  println()

  bootServer()

  def bootServer() {
    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("truss-webapp")

    // create and start our service actor
    val service = system.actorOf(Props[MainServer], "server")

    // start a new HTTP server on port 8080 with our service actor as the handler
    IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)

    println("Hit any key to exit.")
    val result = readLine()
    system.shutdown()
  }

  def openInputSet = new File(getClass.getResource("/sample_input_set.txt").getPath)

  def ERROR = "| ERROR | "

}


