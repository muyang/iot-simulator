package com.iot.app.kafka.producer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.sql.Timestamp;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.iot.app.kafka.util.PropertyFileReader;
import com.iot.app.kafka.vo.IoTData;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * IoT data event producer class which uses Kafka producer for events. 
 * 
 * @author Mu
 *
 */
public class IoTDataProducer {
	
	private static final Logger logger = Logger.getLogger(IoTDataProducer.class);

	public static void main(String[] args) throws Exception {
		//read config file
		Properties prop = PropertyFileReader.readPropertyFile();		
		String zookeeper = prop.getProperty("com.iot.app.kafka.zookeeper");
		String brokerList = prop.getProperty("com.iot.app.kafka.brokerlist");
		String topic = prop.getProperty("com.iot.app.kafka.topic");
		logger.info("Using Zookeeper=" + zookeeper + " ,Broker-list=" + brokerList + " and topic " + topic);

		// set producer properties
		Properties properties = new Properties();
		properties.put("zookeeper.connect", zookeeper);
		properties.put("metadata.broker.list", brokerList);
		properties.put("request.required.acks", "1");
		properties.put("serializer.class", "com.iot.app.kafka.util.IoTDataEncoder");
		//generate event
		Producer<String, IoTData> producer = new Producer<String, IoTData>(new ProducerConfig(properties));
		IoTDataProducer iotProducer = new IoTDataProducer();
		iotProducer.generateIoTEvent(producer,topic);		
	}


	/**
	 * Loop and generates random IoT data in JSON format. 
	 */
	private void generateIoTEvent(Producer<String, IoTData> producer, String topic) throws InterruptedException {
		List<String> placeList = Arrays.asList(new String[]{"place-A", "place-B", "place-C"});
		String Id1 = UUID.randomUUID().toString();
		String Id2 = UUID.randomUUID().toString();
		String Id3 = UUID.randomUUID().toString();
		List<String> IdList = Arrays.asList(new String[]{Id1, Id2, Id3});
		Random rand = new Random();
		logger.info("Sending events");
		// generate event in loop
		while (true) {
			List<IoTData> eventList = new ArrayList<IoTData>();
			for (int i = 0; i < 3; i++) {              // create 3 devices
				String placeId = placeList.get(i);
				
				String deviceId = IdList.get(i); //UUID.randomUUID().toString();
				String temperature = getTemperature(placeId);
				IoTData.locationBean location = new IoTData.locationBean();
				
				
				String latlon = getLatLon(placeId);
				String lat = latlon.substring(0, latlon.indexOf(","));
				String lon = latlon.substring(latlon.indexOf(",") + 1, latlon.length());
				location.setLatitude(lat);
				location.setLongitude(lon);
				
				Timestamp time = new Timestamp(System.currentTimeMillis()); //Date().getTime();
				
				IoTData event = new IoTData(deviceId, temperature, location, time);
				eventList.add(event);
								
/*
				for (int j = 0; j < 5; j++) {// Add 5 events for each device
					String temperature = getTemperature(placeId);
					//IoTData data = new IoTData(deviceId, temperature, location, time);
					//data = String.format("data:%s", data);
					//eventList.add(event);
				}		
*/
			}
			Collections.shuffle(eventList);// shuffle for random events
			for (IoTData event : eventList) {
				KeyedMessage<String, IoTData> data = new KeyedMessage<String, IoTData>(topic, event);
				producer.send(data);
				Thread.sleep(rand.nextInt(1000) + 1000);//random delay of 1 to 2 seconds
			}
		}
	}

	//Method to generate temperature for devices
	private String getTemperature(String placeId) {
		Random rand = new Random();
		int T = 0;
		if (placeId.equals("place-A")) {
			T = 33;
		} else if (placeId.equals("place-B")) {
			T = 24;
		} else if (placeId.equals("place-C")) {
			T = 12;
		} 
		T = T + rand.nextInt(5);   //rand.nextInt(40) - 10; //-10 ~ 30 C
		return String.valueOf(T);
	}
	//Method to generate random latitude and longitude for devices
	private String getLatLon(String placeId) {
			Random rand = new Random();
			double lat0 = 0;
			double lon0 = 0;
			if (placeId.equals("place-A")) {
				lat0 = 52.14691120000001;
				lon0 = 11.65883869999933;
			} else if (placeId.equals("place-B")) {
				lat0 = 34.14587000000005;
				lon0 = -9.85245888888887;
			} else if (placeId.equals("place-C")) {
				lat0 = 55.88888887777777;
				lon0 = -66.66666666666667;
			} 
			//considering mobile devices
			//Float lat = lat0 + rand.nextFloat();
			//Float lon = lon0 + rand.nextFloat();
			//double speed = rand.nextInt(100 - 20) + 20;// random speed between 20 to 100
			return lat0 + "," + lon0;
			//return String.format("latitude:%s,longitude:%s", lat0, lon0);
	}

}
