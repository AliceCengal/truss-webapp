package edu.vanderbilt.truss.struct

import java.util
import scala.collection.JavaConverters._
import com.google.gson.JsonObject
import edu.vanderbilt.truss
import spray.httpx.marshalling.Marshaller
import spray.http.MediaTypes.`application/json`
import spray.http.{AllOrigins, HttpHeaders, HttpEntity}
import java.io.StringWriter
import com.google.gson.stream.JsonWriter

/**
 * Created by athran on 5/4/14.
 */
case class ResultSet(userId: String,
                     inputSetId: String,
                     timeStamp: Int,
                     message: String,
                     isSuccessful: Boolean,
                     responseCode: Int,
                     jointResults: Set[JointResult],
                     memberResults: Set[MemberResult])
    extends truss.ResultStruct
{
  def jointSet(): util.Set[truss.JointResultStruct] =
    jointResults.asInstanceOf[Set[truss.JointResultStruct]].asJava

  def memberSet(): util.Set[truss.MemberResultStruct] =
    memberResults.asInstanceOf[Set[truss.MemberResultStruct]].asJava

  def writeToJson(): String = {
    import ResultSet._
    val buffer = new StringWriter()
    val writer = new JsonWriter(buffer)

    writer.
        beginObject().
        name(KEY_ID).value(userId).
        name(KEY_INPUTID).value(inputSetId).
        name(KEY_TIME).value(Integer.toString(timeStamp)).
        name(KEY_MESSAGE).value(message).
        name(KEY_SUCCESS).value(isSuccessful).
        name(KEY_RESPONSE).value(responseCode)

    writer.name(KEY_JOINTS).beginArray()
    jointResults.foreach { _.writeToJson(writer) }
    writer.endArray()

    writer.name(KEY_MEMBERS).beginArray()
    memberResults.foreach { _.writeToJson(writer) }
    writer.endArray()

    buffer.toString
  }
}

object ResultSet {
  val KEY_ID       = "userId"
  val KEY_INPUTID  = "inputSetId"
  val KEY_TIME     = "timestamp"
  val KEY_MESSAGE  = "message"
  val KEY_SUCCESS  = "isSuccessful"
  val KEY_RESPONSE = "responceCode"
  val KEY_JOINTS   = "jointResultSet"
  val KEY_MEMBERS  = "memberResultSet"

  def fromJson(json: JsonObject): ResultSet = {
    import edu.vanderbilt.truss.util.GsonConversion._

    ResultSet(
               userId = json.get(KEY_ID),
               inputSetId = json.get(KEY_INPUTID),
               timeStamp = json.get(KEY_TIME),
               message = json.get(KEY_MESSAGE),
               isSuccessful = json.get(KEY_SUCCESS),
               responseCode = json.get(KEY_RESPONSE),
               jointResults =
                   json.get(KEY_JOINTS).
                       map(elem => JointResult.fromJson(elem.getAsJsonObject)).
                       toSet,
               memberResults =
                   json.get(KEY_MEMBERS).
                       map(elem => MemberResult.fromJson(elem.getAsJsonObject)).
                       toSet)
  }

  implicit val ResultSetMarshaller =
    Marshaller.of[ResultSet](`application/json`) { (value, contentType, ctx) =>
      ctx.marshalTo(
                     HttpEntity(contentType, value.writeToJson()),
                     HttpHeaders.`Access-Control-Allow-Origin`(AllOrigins))
    }

}