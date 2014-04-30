package edu.vanderbilt.truss

import edu.vanderbilt.truss.engine.EngineUtil
import edu.vanderbilt.truss.parser.ParserUtil
import edu.vanderbilt.truss.reporter.ReporterUtil

object TrussApp extends App {
  println("===============================================================")
  println("|           Truss Webapp                                      |")
  println("===============================================================")
  println()


  // run tests for the Parser and the Reporter. If both pass,
  // run test for Engine, otherwise don't.
  if ((parserTest() || reporterTest()) && engineTest()) {
    println("| SUCCESS | All test passed")
  } else {
    println("| FAILURE | Please try again")
  }

  def ERROR = "| ERROR | "

  def parserTest(): Boolean = {
    println("Running Parser test")

    val parser = ParserUtil.getParser("")

    if (parser == null) {
      print(ERROR)
      println("1. ParserUtil does not construct a proper parser object")
      return false
    }

    val input = parser.parse()

    if (!input.studentId().equals("John Doe")) {
      print(ERROR)
      println("2. Failed to parse student ID")
      return false
    }

    if (!input.inputSetId().equals("Problem#1")) {
      print(ERROR)
      println("3. Failed to parse inputSet ID")
      return false
    }

    if (input.joints() == null || input.joints().isEmpty) {
      print(ERROR)
      println("4. Failed to parse any Joints")
      return false
    }

    if (input.joints().size() != 6) {
      print(ERROR)
      println("5. Failed to parse all Joints.")
      print("   This many joints are parsed: ")
      print(input.joints().size())
      return false
    }

    for (joint <- input.joints()) {
      println(joint)
    }

    true
  }

  def reporterTest(): Boolean = {
    false
  }

  def engineTest(): Boolean = {
    false
  }
}


