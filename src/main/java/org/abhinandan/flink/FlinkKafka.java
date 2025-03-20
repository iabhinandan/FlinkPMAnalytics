package org.abhinandan.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

public class FlinkKafka {

    public static void main(String[] args) throws Exception {
        // Set up the Flink execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Create a Kafka source
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092") // Kafka broker
                .setTopics("pm_data_topic") // Kafka topic to consume
                .setGroupId("my_consumer_group") // Consumer group
                .setStartingOffsets(OffsetsInitializer.earliest()) // Start consuming from the beginning
                .setValueOnlyDeserializer(new SimpleStringSchema()) // Deserializer for message values
                .build();

        // Read from Kafka source
        DataStream<String> kafkaStream = env.fromSource(
                kafkaSource,
                org.apache.flink.api.common.eventtime.WatermarkStrategy.noWatermarks(),
                "Kafka Source");

        // Print consumed messages to the console (example processing)
        kafkaStream.print();


        // Execute the Flink job
        env.execute("Flink Kafka Example");
    }
}

