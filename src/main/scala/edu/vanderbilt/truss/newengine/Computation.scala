package edu.vanderbilt.truss.newengine

import scala.collection.JavaConverters._

import edu.vanderbilt.truss.struct._
import edu.vanderbilt.truss.reporter._
import edu.vanderbilt.truss.engine._
import edu.vanderbilt.truss._

object Computation {
  
  def apply(input: InputSet): ResultSet = {
    val resultHolder = new ResultHolder(input)
    val engine = EngineUtil.getEngine
    engine.injectInputData(input)
    engine.injectOutput(resultHolder)
    engine.compute()
    
    resultHolder.value.getOrElse(ResultSet(message = "Computation Error"))
  }
  
  class ResultHolder(val input: InputSet) extends ResultSetReporter {
    
    var value: Option[ResultSet] = None
    
    override def report(result: ResultStruct): Unit = {
      value = Some(transform(result, input))
    }
  }
  
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



