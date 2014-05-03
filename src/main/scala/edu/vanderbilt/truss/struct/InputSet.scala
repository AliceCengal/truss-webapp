package edu.vanderbilt.truss.struct

import com.google.gson.JsonObject

/**
 * Marshalled form of user input
 *
 * Created by athran on 5/3/14.
 */
case class InputSet(userId: String,
                    inputSetId: String,
                    jointSet: Set[Joint],
                    memberSet: Set[Member])

object InputSet {
  val KEY_ID       = "userId"
  val KEY_INPUT_ID = "inputSetId"
  val KEY_JOINTS   = "jointSet"
  val KEY_MEMBERS  = "memberSet"

  def fromJson(json: JsonObject): InputSet = {
    import edu.vanderbilt.truss.util.GsonConversion._

    InputSet(
              userId = json.get(KEY_ID),
              inputSetId = json.get(KEY_INPUT_ID),
              jointSet =
                  json.get(KEY_JOINTS).
                      map(elem => Joint.fromJson(elem.getAsJsonObject)).
                      toSet,
              memberSet =
                  json.get(KEY_MEMBERS).
                      map(elem => Member.fromJson(elem.getAsJsonObject)).
                      toSet
            )
  }

}
