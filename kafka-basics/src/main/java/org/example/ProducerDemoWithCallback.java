package org.example;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());

    public static void main(String[] args) {

        log.info("I am a kafka producer");

        // create Producer Properties
        Properties properties = new Properties();

        // connect to local host
        properties.setProperty("bootstrap.servers","127.0.0.1:9092");

        // set producer properties
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        //properties.setProperty("batch.size", "400");

        // create the Producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);


        for (int j=0; j<10; j++) {
            for (int i = 0; i < 30; i++) {
                // create a producer record
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "hello world" + i);

                // send data
                producer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        // executed everytime a record is successfully sent or an exception is thrown
                        if (e == null) {
                            log.info("Received new metadata \n" +
                                    "Topic:" + metadata.topic() + "\n" +
                                    "Partition:" + metadata.partition() + "\n" +
                                    "Offset:" + metadata.offset() + "\n" +
                                    "Timestamp:" + metadata.timestamp());
                        } else {
                            log.error("Error while executing");
                        }
                    }
                });
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        // flush and close the producer
        producer.flush();
        producer.close();


    }
}
