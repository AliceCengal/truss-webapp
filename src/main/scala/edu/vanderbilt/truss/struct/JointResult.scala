package edu.vanderbilt.truss.struct

import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter
import edu.vanderbilt.truss

/**
 * Created by athran on 5/3/14.
 */
case class JointResult(id: Int,
                       displacement: (Double,Double),
                       reaction: (Double,Double))
    extends truss.JointResultStruct
{
  import JointResult._

  def displacementX(): Double = displacement._1

  def displacementY(): Double = displacement._2

  def reactionX(): Double = reaction._1

  def reactionY(): Double = reaction._2

  def writeToJson(writer: JsonWriter) {
    writer.
        beginObject().
        name(KEY_ID).value(id).
        name(KEY_DISP_X).value(displacementX()).
        name(KEY_DISP_Y).value(displacementY()).
        name(KEY_REACT_X).value(reactionX()).
        name(KEY_REACT_Y).value(reactionY()).
        endObject()
  }

}

object JointResult {
  val KEY_ID      = "id"
  val KEY_DISP_X  = "displacementX"
  val KEY_DISP_Y  = "displacementY"
  val KEY_REACT_X = "reactionX"
  val KEY_REACT_Y = "reactionY"

  def fromJson(json: JsonObject): JointResult = {
    import edu.vanderbilt.truss.util.GsonConversion._

    JointResult(
                 id = json.get(KEY_ID),
                 displacement = (
                     json.get(KEY_DISP_X),
                     json.get(KEY_DISP_Y)),
                 reaction = (
                     json.get(KEY_REACT_X),
                     json.get(KEY_REACT_Y))
               )
  }

  implicit def from(result: truss.JointResultStruct): JointResult = {
    JointResult(
                 id = result.id(),
                 displacement = (
                     result.displacementX(),
                     result.displacementY()),
                 reaction = (
                     result.reactionX(),
                     result.reactionY()))
  }

}

