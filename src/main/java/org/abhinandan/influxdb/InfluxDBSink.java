//package org.abhinandan.influxdb;
//
//import com.influxdb.client.InfluxDBClient;
//import com.influxdb.client.InfluxDBClientFactory;
//import com.influxdb.client.WriteApiBlocking;
//import com.influxdb.client.domain.WritePrecision;
//import com.influxdb.client.write.Point;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
//import org.apache.flink.streaming.api.datastream.DataStream;
//
//import java.time.Instant;
//
//public class InfluxDBSink {
//    private transient InfluxDBClient influxDBClient;
//    private transient WriteApiBlocking writeApi;
//
//    private final String url;
//    private final String token;
//    private final String org;
//    private final String bucket;
//
//    // Constructor to initialize connection parameters
//    public InfluxDBSink(String url, String token, String org, String bucket) {
//        this.url = url;
//        this.token = token;
//        this.org = org;
//        this.bucket = bucket;
//    }
//
//    @Override
//    public void open(Configuration parameters) throws Exception {
//        // Initialize InfluxDB client and write API
//        influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
//        writeApi = influxDBClient.getWriteApiBlocking();
//    }
//
//    @Override
//    public void invoke(String value, Context context) throws Exception {
//        // Create a Point (data to be written to InfluxDB)
//        Point point = Point.measurement("measurement_name") // Measurement name
//                .addTag("tag_name", "tag_value")              // Tags
//                .addField("field_name", value)                // Field to write
//                .time(Instant.now(), WritePrecision.NS);      // Timestamp with precision (nanoseconds)
//
//        // Write the point to InfluxDB
//        writeApi.writePoint(point);
//    }
//
//    @Override
//    public void close() throws Exception {
//        // Close resources when the sink is closed
//        if (writeApi != null) {
//            writeApi.close();
//        }
//        if (influxDBClient != null) {
//            influxDBClient.close();
//        }
//    }
//}
