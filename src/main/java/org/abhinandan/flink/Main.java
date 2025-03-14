package org.abhinandan.flink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {
    public static void main(String[] args) throws Exception {
        // Create Flink execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();

        // Create a simple DataStream
        DataStream<String> text = env.fromElements("Hello", "Flink", "World!");

        // Process DataStream
        DataStream<String> result = text.map((MapFunction<String, String>) value -> "Processed: " + value).rebalance();

        // Print the output
        result.print();

        // Execute Flink job
        env.execute("Flink Hello World");
    }
}