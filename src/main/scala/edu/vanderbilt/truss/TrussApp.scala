package edu.vanderbilt.truss

import java.io.{IOException, File, FileInputStream}

import scala.collection.JavaConverters._
import scala.io.Source

import edu.vanderbilt.truss.parser.ParserUtil
import edu.vanderbilt.truss.legacy.Truss2D

object TrussApp extends App {
  println("===============================================================")
  println("|                         Truss Webapp                        |")
  println("===============================================================")
  println()

  legacyTest()

  def legacyTest() {

    for (
      line <- Source.fromFile(openInputSet).getLines()
    ) {
      println(line)
    }
    println()

    val dataSource = new FileInputStream(
        new File(getClass.getResource("/sample_input_set.txt").getPath))

    val legacyEngine = new Truss2D(dataSource,
                                   System.out)
    try {
      legacyEngine.run()

      println()
      println("Legacy test successful")
    } catch {
      case e: IOException =>
        println("Parsing fail")
        println(e.getMessage)
      case e: NumberFormatException =>
        println("Numerical fail")
        println(e.getMessage)
    }
  }

  def openInputSet = new File(getClass.getResource("/sample_input_set.txt").getPath)

  def ERROR = "| ERROR | "

  // run tests for the Parser and the Reporter. If both pass,
  // run test for Engine, otherwise don't.
  def runComponentTest() {
    if ((parserTest() || reporterTest()) && engineTest()) {
      println("| SUCCESS | All test passed")
    } else {
      println("| FAILURE | Please try again")
    }
  }

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

    for (joint <- input.joints().asScala) {
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


