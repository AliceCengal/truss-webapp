package edu.vanderbilt.truss.struct

import com.google.gson.{JsonParser, JsonObject}
import spray.httpx.unmarshalling.Unmarshaller
import spray.http.MediaTypes.`application/json`
import spray.http.HttpEntity
import edu.vanderbilt.truss
import com.google.gson.stream.JsonWriter

/**
 * Created by athran on 5/3/14.
 */
case class Joint(id: Int,
                 coor: (Double, Double),
                 restraint: (Boolean, Boolean),
                 load: (Double, Double))
    extends truss.Joint
{
  def xCoor(): Double = coor._1

  def yCoor(): Double = coor._2

  def isRestraintX: Boolean = restraint._1

  def isRestraintY: Boolean = restraint._2

  def loadX(): Double = load._1

  def loadY(): Double = load._2

  def writeToJson(writer: JsonWriter) {
    import edu.vanderbilt.truss.util.GsonConversion._
    import Joint._

    writer.obj { _.
      name(KEY_ID).value(id).
      name(KEY_X).value(xCoor()).
      name(KEY_Y).value(yCoor()).
      name(KEY_REST_X).value(isRestraintX).
      name(KEY_REST_Y).value(isRestraintY).
      name(KEY_LOAD_X).value(loadX()).
      name(KEY_LOAD_Y).value(loadY())
    }
  }

}


object Joint {
  val KEY_ID     = "id"
  val KEY_X      = "x"
  val KEY_Y      = "y"
  val KEY_REST_X = "isRestraintX"
  val KEY_REST_Y = "isRestraintY"
  val KEY_LOAD_X = "loadX"
  val KEY_LOAD_Y = "loadY"

  def fromJson(json: JsonObject): Joint = {
    import edu.vanderbilt.truss.util.GsonConversion._

    Joint(
           id = json.get(KEY_ID),
           coor = (
               json.get(KEY_X),
               json.get(KEY_Y)),
           restraint = (
               json.get(KEY_REST_X),
               json.get(KEY_REST_Y)),
           load = (
               json.get(KEY_LOAD_X),
               json.get(KEY_LOAD_Y))
         )
  }

  implicit val JointUnmarshaller = Unmarshaller[Joint](`application/json`) {
    case HttpEntity.NonEmpty(contentType, data) =>
      fromJson(new JsonParser().parse(data.asString).getAsJsonObject)
  }

  def compatCreateJoint(id: Int,
                        cx: Double,
                        cy: Double,
                        rx: Boolean,
                        ry: Boolean,
                        lx: Double,
                        ly: Double): truss.Joint = {
    Joint(id = id, coor = (cx, cy), restraint = (rx, ry), load = (lx, ly))
  }

}