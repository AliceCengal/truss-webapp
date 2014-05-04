package edu.vanderbilt.truss.struct

import java.util
import scala.collection.JavaConverters._
import com.google.gson.{JsonParser, JsonObject}
import spray.httpx.unmarshalling.Unmarshaller
import spray.http.MediaTypes.`application/json`

import edu.vanderbilt.truss.InputStruct
import edu.vanderbilt.truss
import spray.http.HttpEntity

/**
 * Marshalled form of user input
 *
 * Created by athran on 5/3/14.
 */
case class InputSet(userId: String,
                    inputSetId: String,
                    jointSet: Set[Joint],
                    memberSet: Set[Member])
    extends InputStruct
{
  def studentId(): String = userId

  def joints(): util.Set[truss.Joint] = {
    // MAGIC!!!
    jointSet.map(_.asInstanceOf[truss.Joint]).asJava
  }

  def members(): util.Set[truss.Member] = {
    // No seriously, how the hell could this even work?
    memberSet.map(_.asInstanceOf[truss.Member]).asJava
  }
}

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

  implicit val InputSetUnmarshaller =
      Unmarshaller[InputSet](`application/json`) {
        case HttpEntity.NonEmpty(contentType, data) =>
          fromJson(new JsonParser().parse(data.asString).getAsJsonObject)
      }

}
