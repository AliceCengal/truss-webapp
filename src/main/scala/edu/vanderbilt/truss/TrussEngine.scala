package edu.vanderbilt.truss

import scala.collection.JavaConverters._
import struct._
import reporter._
import engine._
import InputSet._

class TrussEngine(private val input: InputSet) {
  import TrussEngine._
  
  def compute: ResultSet = {
    val resultHolder = new ResultHolder(input)
    val engine = EngineUtil.getEngine
    engine.injectInputData(input)
    engine.injectOutput(resultHolder)
    engine.compute()
    
    resultHolder.value
      .getOrElse(ResultSet(message = "Computation Error"))
  }
  
  
}

object TrussEngine {
  
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
