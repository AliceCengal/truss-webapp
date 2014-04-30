package edu.vanderbilt.truss

import java.io.{IOException, File, FileInputStream}
import scala.io.Source
import edu.vanderbilt.truss.legacy.Truss2D

/**
 * Test the legacy system from 1998.
 *
 * Created by athran on 4/30/14.
 */
trait LegacyTest {

  def legacyTest() {

    for (
      line <- Source.
                  fromFile(TrussApp.openInputSet).
                  getLines()
    ) {
      println(line)
    }
    println()

    val dataSource = new FileInputStream(
        new File(getClass.getResource("/sample_input_set.txt").getPath))

    val legacyEngine = new Truss2D(System.out)
    try {
      legacyEngine.injectData(dataSource)
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

}
