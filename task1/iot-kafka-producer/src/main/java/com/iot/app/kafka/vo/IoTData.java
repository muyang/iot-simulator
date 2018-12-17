package com.iot.app.kafka.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Class to represent the IoT temperature data.
 * 
 * @author Mu
 *
 */
/*
public class Data {
	private IoTData data;
	public IoTData getData(){
		return data;
	}
	public void setIoTData (IoTData data){
		this.data = data;
	}
	
	public Data() {}
	public Data(IoTData data) {
		super();
		this.data = data;
	}
*/

public class IoTData {
	private String deviceId;
    private String temperature;
    private locationBean location;
    private Timestamp time;
    
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }	
        
    public String getTemperature() {
        return temperature;
    }
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public locationBean getLocation() {
        return location;
    }
    public void setLocation(locationBean location) {
        this.location = location;
    }

    public static class locationBean {
        private String latitude;
        private String longitude;

        public String getLatitude() {
            return latitude; 
            //return String.format("latitude:%s,longitude:%s", lat, lon);
        }
        public String getLongitude() {
            return longitude;
        }
        
        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }        
    }
    
    public Timestamp getTime() {
        return time;
    }
    public void setTime(Timestamp time) {
        this.time = time;
    }
   
	public IoTData(){
		
	}
	
	public IoTData(String deviceId, String temperature, locationBean location, Timestamp time) {
		super();
		this.deviceId = deviceId;
		this.temperature = temperature;
		this.location = location;
		this.time = time;
	}
}