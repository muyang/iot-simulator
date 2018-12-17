/**
 *
 * @author Mu
 *
 */

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import top.spoofer.hbrdd.config.HbRddConfig
import top.spoofer.hbrdd._

object KafkaStreamingToHbase {
  private val sparkMaster = "localhost"       
  private val sparkMasterPort = "7077"
  private val zkQuorum = "localhost:2181"     //"Core:2181"
  private val group = "test"
  private val topics = "iot-data-event"       //sensor
  private val numThreads = "1"
  private val brokers = "localhost:9092"      //"Kafka1:9092,Kafka1:9093,Kafka2:9094"

  /**
    * Map(qualifier, value)
    * @param sensor SensorInfos
    * @return
    */
  implicit def sensorInfoToMap(sensor: SensorInfos): Map[String, String] = {
    Map("temperature" -> sensor.temperature, "location" -> sensor.location, "time" -> sensor.time)
  }

  def main(args: Array[String]) {
    implicit val hbConfig = HbRddConfig()

    TableManager.createTable(force = false)
    println("created table: KafkaToHbase, family is: SensorInfos .....")

    // Create context with 5 second batch interval
    val sparkConf = new SparkConf().setAppName("DirectKafkaWordCount").setMaster(s"spark://$sparkMaster:$sparkMasterPort")
      .setJars(List("./out/artifacts/spark_streaming_kafka_jar/spark-streaming-kafka.jar"))
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)

    val sensorsLine = messages.map(_._2)  //get data
    val sensorInfosData = sensorsLine map { sensorInfosStr =>
      SensorInfosExtractor.extractSensorInfos(sensorInfosStr) //extract data
    } filter(_.isDefined) map {_.get}

    /**
      * make the content as the format that hbrdd requires
      * rdd(rowid, Map(column, value))
      */
    val dataToHbase = sensorInfosData map { sensorInfo =>
      sensorInfo.deviceId -> sensorInfoToMap(sensorInfo)
    }

    dataToHbase.foreachRDD(rdd => rdd.put2Hbase("KafkaToHbase", "SensorInfos"))

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }
}
