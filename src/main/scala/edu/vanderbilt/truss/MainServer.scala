package edu.vanderbilt.truss

import scala.concurrent.duration._

import akka.actor.{Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import spray.routing.HttpService

import edu.vanderbilt.truss.struct.InputSet
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
            complete {
              Computation(input)
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
    
  def sample = complete(InputSet.fromLegacy(ParserUtil.getParser("").parse()))

  def notFoundPage = getFromResource("webpage/not_found.html")

  def indexPage = getFromResource("webpage/index.txt")

}
