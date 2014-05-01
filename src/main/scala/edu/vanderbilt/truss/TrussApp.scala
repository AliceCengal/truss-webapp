package edu.vanderbilt.truss

import java.io.File
import edu.vanderbilt.truss.reporter.ReporterUtil

object TrussApp extends App with LegacyTest {

  println("===============================================================")
  println("|                         Truss Webapp                        |")
  println("===============================================================")
  println()

  val reporter = ReporterUtil.getReporter(System.out)
  reporter.report(ResultFactory.dummyResult())

  def openInputSet = new File(getClass.getResource("/sample_input_set.txt").getPath)

  def ERROR = "| ERROR | "

}


