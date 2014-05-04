package edu.vanderbilt.truss

import scala.concurrent.duration._

import akka.actor.Actor
import akka.util.Timeout

import spray.routing.HttpService


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
      pathPrefix("user" / Segment) {
        userName =>
          pathEnd {
            get {
              complete("Return info for user " + userName)
            } ~
            post {
              complete("Register user " + userName)
            }
          } ~
          path("inputset") {
            get {
              complete("return a list of InputSet ids for user " + userName)
            } ~
            post {
              complete("create a new InputSet record for user " + userName)
            }
          } ~
          path("inputset" / Segment) {
            inputSetId =>
              get {
                complete("Return the latest InputSet of user " + userName + " with id " + inputSetId)
              } ~
              post {
                complete("Edit the InputSet record of user " + userName + " with id " + inputSetId)
              }
          }
      } ~
      path("computation") {
        post {
          complete("Anonymous computation")
        }
      } ~
      path("sample") {
        get {
          complete("return a list of sample Ids")
        }
      } ~
      path("sample" / Segment) {
        inputSetId =>
          get {
            complete("return a sample InputSet with id " + inputSetId)
          }
      } ~
      indexPage
    } ~
    getFromResourceDirectory("webpage")

  def notFoundPage = getFromResource("webpage/not_found.html")

  def indexPage = getFromResource("webpage/index.txt")

}
