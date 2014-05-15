package edu.vanderbilt.truss.struct

import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter

import edu.vanderbilt.truss
import edu.vanderbilt.truss.util.GsonConversion._
import MemberResult._

/**
 * Created by athran on 5/4/14.
 */
case class MemberResult(id: Int,
                        force: Double)
    extends truss.MemberResultStruct
{
  def memberForce(): Double = force

  def writeToJson(writer: JsonWriter) {
    writer.obj { _.
        name(KEY_ID).value(id).
        name(KEY_FORCE).value(force)
    }
  }
}

object MemberResult {
  val KEY_ID    = "id"
  val KEY_FORCE = "force"

  def fromJson(json: JsonObject): MemberResult = {
    MemberResult(
                  id = json.get(KEY_ID),
                  force = json.get(KEY_FORCE)
                )
  }

  def from(struct: truss.MemberResultStruct): MemberResult = {
    MemberResult(id = struct.id(), force = struct.memberForce())
  }

}