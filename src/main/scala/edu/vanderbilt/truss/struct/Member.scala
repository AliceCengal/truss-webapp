package edu.vanderbilt.truss.struct

import com.google.gson.JsonObject

/**
 * Created by athran on 5/3/14.
 */
case class Member(id: Int,
                  joints: (Int, Int),
                  area: Double,
                  elastivity: Double)

object Member {
  val KEY_ID    = "id"
  val KEY_LEFT  = "jointLeft"
  val KEY_RIGHT = "jointRight"
  val KEY_AREA  = "area"
  val KEY_ELAS  = "elasticity"

  def fromJson(json: JsonObject): Member = {
    import edu.vanderbilt.truss.util.GsonConversion._

    Member(
            id = json.get(KEY_ID),
            joints = (
                json.get(KEY_LEFT),
                json.get(KEY_RIGHT)),
            area = json.get(KEY_AREA),
            elastivity = json.get(KEY_ELAS)
          )
  }

}
