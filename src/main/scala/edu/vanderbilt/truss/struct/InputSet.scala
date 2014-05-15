package edu.vanderbilt.truss.struct

import java.util
import java.io.StringWriter
import scala.collection.JavaConverters._

import com.google.gson.{JsonParser, JsonObject}
import com.google.gson.stream.JsonWriter
import spray.httpx.unmarshalling.Unmarshaller
import spray.http.MediaTypes.`application/json`
import spray.http.{AllOrigins, HttpHeaders, HttpEntity}

import edu.vanderbilt.truss.InputStruct
import edu.vanderbilt.truss
import edu.vanderbilt.truss.util.GsonConversion._
import InputSet._
import spray.httpx.marshalling.Marshaller

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

  def writeToJson: String = {
    val buffer = new StringWriter
    val writer = new JsonWriter(buffer)
    writer.setIndent("")

    writer.obj { _.
      name(KEY_ID).value(userId).
      name(KEY_INPUT_ID).value(inputSetId).
      name(KEY_JOINTS).array(w => jointSet.foreach(_.writeToJson(w))).
      name(KEY_MEMBERS).array(w => memberSet.foreach(_.writeToJson(w)))
    }

    buffer.toString
  }
}

object InputSet {
  val KEY_ID       = "studentId"
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

  def fromLegacy(legacy: InputStruct): InputSet = {
    InputSet(userId = legacy.studentId(),
              inputSetId = legacy.inputSetId(),
              jointSet = legacy.joints().asScala.map(_.asInstanceOf[Joint]).toSet,
              memberSet = legacy.members().asScala.map(_.asInstanceOf[Member]).toSet)
  }

  implicit val InputSetUnmarshaller =
      Unmarshaller[InputSet](`application/json`) {
        case HttpEntity.NonEmpty(contentType, data) =>
          fromJson(new JsonParser().parse(data.asString).getAsJsonObject)
      }

  implicit val InputSetMarshaller =
    Marshaller.of[InputSet](`application/json`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, value.writeToJson),
                     HttpHeaders.`Access-Control-Allow-Origin`(AllOrigins))
    }

}
