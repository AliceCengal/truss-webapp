package edu.vanderbilt.truss

import scala.concurrent.duration._

import akka.actor.Actor
import akka.util.Timeout

import spray.routing.{PathMatchers, HttpService}


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

  implicit val timeout = Timeout(5 seconds)

  val mainRoute =
    pathPrefix("api") {
      pathPrefix("user" / PathMatchers.Segment) {
        userName =>
          pathPrefix("history") {
            path("last") {
              complete("Requesting the last computation of user " + userName)
            } ~
            path("previousn" / IntNumber) {
              num =>
                complete("Requesting last " + num + " computations for user " + userName)
            } ~
            path("all") {
              complete("Requesting all computations from user " + userName)
            } ~
            indexPage
          } ~
          path("compute") {
            complete("Do computation for user " + userName)
          } ~
          path("") {
            complete("Received from user " + userName)
          } ~
          indexPage
      } ~
      path("compute") {
        complete("free computation")
      } ~
      indexPage
    } ~
    path("compute") {
      complete("free computation")
    } ~
    getFromResourceDirectory("webpage")

  def notFoundPage = getFromResource("webpage/not_found.html")

  def indexPage = getFromResource("webpage/index.txt")

}
