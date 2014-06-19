package edu.vanderbilt.truss

import java.io.File
import scala.concurrent.duration._
import scala.util.control.NonFatal

import akka.actor.{Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import spray.routing.HttpService
import com.google.gson.JsonParser

import edu.vanderbilt.truss.struct._
import edu.vanderbilt.truss.parser.ParserUtil
import edu.vanderbilt.truss.newengine._

/**
 * The entry point of the server.
 *
 * Created by athran on 5/3/14.
 */
class MainServer extends Actor with MainService {

  def actorRefFactory = context

  def receive: Actor.Receive = runRoute(mainRoute)

}

trait MainService extends HttpService {

  implicit def executionContext = actorRefFactory.dispatcher

  implicit val timeout = Timeout(5.seconds)

  val mainRoute =
    pathPrefix("api") {
      path("computation") {
        post {
          // Deserialize the json into InputSet. See `InputSet.InputSetUnmarshaller`
          entity(as[InputSet]) { input =>
            println()
            println(input.writeToJson)
            val res = Computation(input)
            println(res)
            try {
              val str = res.writeToJson
              complete(str)
            } catch {
              case NonFatal(e) => complete(ResultSet(message = "Computation error"))
            }
          }
        }
      } ~
      path("sample") {
        get {
          sample
        }
      } ~
      path("sample" / Segment) {
        inputSetId =>
          get {
            sample
          }
      } ~
      indexPage
    } ~
    path("") {
      getFromResource("webpage/main.html")
    } ~
    getFromResourceDirectory("webpage")
    
  def sample = complete{
    val file = new File(getClass.getResource("/sample2.txt").getPath)
    val lines = io.Source.fromFile(file).getLines.mkString("\n")
    InputSet.fromJson(new JsonParser().parse(lines).getAsJsonObject)
  }

  def notFoundPage = getFromResource("webpage/not_found.html")

  def indexPage = getFromResource("webpage/index.txt")

}
