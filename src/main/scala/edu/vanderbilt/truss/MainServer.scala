package edu.vanderbilt.truss

import scala.concurrent.duration._

import akka.actor.{Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import spray.routing.HttpService

import edu.vanderbilt.truss.struct.InputSet


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

  val trussComputer = actorRefFactory.actorOf(Props[TrussComputer], "trussComputer")

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
          // Deserialize the json into InputSet. See `InputSet.InputSetUnmarshaller`
          entity(as[InputSet]) { input =>
            complete {
              // Send request to `trussComputer`, asking it to do a computation.
              // The `?` method returns a `Future[Any]`.
              // Then cast the `Future[Any]` to Future[ComputationResult].
              // Store that in `future`.
              val future = (trussComputer ? TrussComputer.Compute(input)).
                           mapTo[TrussComputer.ComputationResult]

              // Extract the result from `Future[ComputationResult(ResultSet)]`.
              // Return the result, which gets serialized into HttpEntity.
              // See `ResultSet.ResultSetMarshaller`
              for (TrussComputer.ComputationResult(result) <- future) { result }
            }
          }
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
