package org.abhinandan.flink;

import org.abhinandan.config.FlinkStreamingConfig;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.time.Instant;

public class InfluxDBTest {
    // Set up the Flink execution environment
    public static void main(String[] args) throws Exception {
        // InfluxDB connection details
        String url = "http://localhost:8086"; // Replace with your InfluxDB URL
        String token = FlinkStreamingConfig.getInfluxDbToken();          // Replace with your API token
        String org = "myorg";              // Replace with your organization name
        String bucket = "pmdata";        // Replace with your bucket name

        // Set up Flink execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Example data stream
        DataStream<SensorData> inputDataStream = env.fromElements(
                new SensorData("sensor1", 23.5, Instant.now().toString()),
                new SensorData("sensor2", 25.0, Instant.now().toString())
        );

        // Map and send data to InfluxDB
        inputDataStream.map(new MapFunction<SensorData, SensorData>() {
            private transient InfluxDBClient influxDBClient;

            @Override
            public SensorData map(SensorData data) throws Exception {
                if (influxDBClient == null) {
                    // Initialize InfluxDB client
                    influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray());
                }

                // Create a point in line protocol
                Point point = Point.measurement("sensor_data")
                        .addTag("sensorId", data.sensorId)
                        .addField("temperature", data.temperature)
                        .time(Instant.parse(data.timestamp), WritePrecision.MS);

                // Write data to InfluxDB
                influxDBClient.getWriteApiBlocking().writePoint(bucket, org, point);

                return data;
            }

            @Override
            protected void finalize() throws Throwable {
                if (influxDBClient != null) {
                    influxDBClient.close();
                }
            }
        });

        // Execute Flink job
        env.execute("Flink to InfluxDB Demo");
    }

    // SensorData class
    public static class SensorData {
        public String sensorId;
        public double temperature;
        public String timestamp;

        public SensorData() {}

        public SensorData(String sensorId, double temperature, String timestamp) {
            this.sensorId = sensorId;
            this.temperature = temperature;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "SensorData{sensorId='" + sensorId + "', temperature=" + temperature + "}";
        }
    }
}
