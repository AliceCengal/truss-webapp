package edu.vanderbilt.truss

import java.io.File
import edu.vanderbilt.truss.reporter.ReporterUtil
import edu.vanderbilt.truss.engine.EngineUtil
import edu.vanderbilt.truss.parser.ParserUtil

object TrussApp extends App with LegacyTest {

  println("===============================================================")
  println("|                         Truss Webapp                        |")
  println("===============================================================")
  println()

  val engine = EngineUtil.getEngine(ParserUtil.getParser(""),
                                     ReporterUtil.getReporter(System.out))
  engine.compute()

  def openInputSet = new File(getClass.getResource("/sample_input_set.txt").getPath)

  def ERROR = "| ERROR | "

}


