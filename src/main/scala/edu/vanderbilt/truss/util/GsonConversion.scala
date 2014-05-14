package edu.vanderbilt.truss.util

import com.google.gson.JsonElement
import com.google.gson.stream.JsonWriter

/**
 * Convert JsonElement type to native Scala types.
 *
 * Created by athran on 5/3/14.
 */
object GsonConversion {

  import scala.collection.JavaConverters._
  import scala.language.implicitConversions

  implicit def jsonToInt(json: JsonElement): Int = json.getAsInt

  implicit def jsonToDouble(json: JsonElement): Double = json.getAsDouble

  implicit def jsonToString(json: JsonElement): String = json.getAsString

  implicit def jsonToBoolean(json: JsonElement): Boolean = json.getAsBoolean

  implicit def jsonToIterable(json: JsonElement): Iterator[JsonElement] = json.getAsJsonArray.iterator().asScala

  implicit def writerToSuper(writer: JsonWriter): SuperWriter = new SuperWriter(writer)

}

class SuperWriter(val writer: JsonWriter) {
  def obj(procedure: JsonWriter => Unit): SuperWriter = {
    writer.beginObject()
    procedure(writer)
    writer.endObject()
    this
  }
  def array(procedure: JsonWriter => Unit): SuperWriter = {
    writer.beginArray()
    procedure(writer)
    writer.endArray()
    this
  }
}

object SuperWriter {

  implicit def superToNormalWriter(sup: SuperWriter): JsonWriter = sup.writer

}