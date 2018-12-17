/** 
 * @author Mu
 *
 * this app is for:
  json parse, extract information, covering:
 * deviceId: sensor id
 * temperature
 * location
 * time
 * then make of SensorInfos and return it
 */

import org.json4s._
import org.json4s.jackson.JsonMethods._


case class SensorInfos(deviceId: String, temperature: String, location: String, time: String)

object SensorInfosExtractor {
  implicit val formats = DefaultFormats

  @inline
  private def extractDeviceId(js: JValue) = {
    (js \\ "deviceId").extractOpt[String]
  }

  @inline
  private def extractTemperature(js:JValue) = {
    (js \\ "temperaure").extractOpt[String]
  }

  @inline
  private def extractLocation(js: JValue) = {
    (js \\ "location").extractOpt[String]
  }

  @inline
  private def extractTime(js: JValue) = {
    (js \\ "time").extractOpt[String]
  }

  @inline
  private def parsingSensorInfos(sensorInfosStr: String): Option[JValue] = {
    try {
      Some(parse(sensorInfosStr))
    } catch {
      case ex: Exception => None
    }
  }

  def extractSensorInfos(sensorInfosStr: String): Option[SensorInfos] = {
    this.parsingSensorInfos(sensorInfosStr) match {
      case None => None
      case Some(sensorInfosJs) =>
        for { //if there is None, yield return None
          deviceId <- this.extractDeviceId(sensorInfosJs)
          temperature <- this.extractTemperature(sensorInfosJs)
          location <- this.extractLocation(sensorInfosJs)
          time <- this.extractTime(sensorInfosJs)
        } yield SensorInfos(deviceId, temperature, location, time)
    }
  }
}
