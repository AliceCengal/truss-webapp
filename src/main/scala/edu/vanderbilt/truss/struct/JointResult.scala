package edu.vanderbilt.truss.struct

import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter

/**
 * Created by athran on 5/3/14.
 */
case class JointResult(id: Int,
                       displacement: (Double,Double),
                       reaction: (Double,Double))
{
  import JointResult._

  def writeJson(writer: JsonWriter) {
    writer.
        beginObject().
        name(KEY_ID).value(id).
        name(KEY_DISP_X).value(displacement._1).
        name(KEY_DISP_Y).value(displacement._2).
        name(KEY_REACT_X).value(reaction._1).
        name(KEY_REACT_Y).value(reaction._2).
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

}

