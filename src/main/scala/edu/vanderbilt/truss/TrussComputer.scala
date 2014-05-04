package edu.vanderbilt.truss

import scala.collection.JavaConverters._
import akka.actor.{ActorLogging, Actor}
import edu.vanderbilt.truss.struct.{MemberResult, JointResult, ResultSet, InputSet}
import edu.vanderbilt.truss.engine.EngineUtil
import edu.vanderbilt.truss.reporter.ResultSetReporter

/**
 * Created by athran on 5/4/14.
 */
class TrussComputer extends Actor with ActorLogging {
  import TrussComputer._

  def receive: Actor.Receive = {
    case Compute(input) => compute(input)
    case _ =>
  }

  private def compute(input: InputSet) {
    engine.injectInputData(input) // Holy shit
    engine.injectOutput(outputPipe(input))
    engine.compute()
  }

  private val engine = EngineUtil.getEngine

  /**
   * Creates an output pipe which receives data in the form
   * of `InputSet`, wraps it in `ComputationResult`, and sends it
   * as a reply to the requester.
   */
  private def outputPipe(input: InputSet) = new ResultSetReporter {
    def report(result: ResultStruct): Unit = {
      sender ! ComputationResult(transform(result, input))
    }
  }

}

object TrussComputer {
  case class Compute(input: InputSet)
  case class ComputationResult(result: ResultSet)

  /**
   * Transform a `ResultStruct` into a `ResultSet`, mixing in
   * data from an `InputSet`
   */
  def transform(from: ResultStruct, input: InputSet): ResultSet = {
    ResultSet(
               userId = input.studentId(),
               inputSetId = input.inputSetId,
               timeStamp = (System.currentTimeMillis() / 1000).asInstanceOf[Int],
               message = from.message(),
               isSuccessful = from.isSuccessful,
               responseCode = 0,
               jointResults = {
                 Set.empty[JointResult] ++ from.jointSet().asScala.map(JointResult.from)
               },
               memberResults = {
                 Set.empty[MemberResult] ++ from.memberSet().asScala.map(MemberResult.from)
               })
  }

}
