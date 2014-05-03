package edu.vanderbilt.truss.struct

import com.google.gson.JsonObject

/**
 * Created by athran on 5/3/14.
 */
case class Joint(id: Int,
                 coor: (Double, Double),
                 restraint: (Boolean, Boolean),
                 load: (Double, Double))

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

}