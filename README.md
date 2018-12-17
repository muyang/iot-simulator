# IoT Temperature Simulator Data Producer based on Kafka

#to compile with maven
mvn package
mvn exec:java -Dexec.mainClass="com.iot.app.kafka.producer.IoTDataProducer"

#to run the jar 
java -jar iot-kafka-producer-1.0.0.jar
