package edu.vanderbilt.truss.struct

import com.google.gson.JsonObject
import edu.vanderbilt.truss

/**
 * Created by athran on 5/3/14.
 */
case class Member(id: Int,
                  joints: (Int, Int),
                  area: Double,
                  elasticity: Double)
    extends truss.Member
{
  def jointLeft(): Int = joints._1

  def jointRight(): Int = joints._2
}

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
            elasticity = json.get(KEY_ELAS)
          )
  }

  def compatCreateMember(id: Int,
                         left: Int,
                         right: Int,
                         area: Double,
                         e: Double): truss.Member = {
    Member(id = id, joints = (left, right), area = area, elasticity = e)
  }

}
