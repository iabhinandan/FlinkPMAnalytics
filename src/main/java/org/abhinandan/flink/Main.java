package org.abhinandan.flink;

import org.abhinandan.config.FlinkStreamingConfig;
import org.abhinandan.pojo.PMKafkaEventPojo;
import org.abhinandan.sampledata.TelecomPMData;
import org.abhinandan.util.FlinkJsonToPojo;
import org.abhinandan.util.FlinkJsonToRowMapper;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;



public class Main {
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // Make sure you are running with one thread locally
        EnvironmentSettings settings = EnvironmentSettings.newInstance().inStreamingMode().build();
        TableEnvironment tableEnv = TableEnvironment.create(settings);

        StreamTableEnvironment stableEnv = StreamTableEnvironment.create(env);



        Configuration config = new Configuration();
        config.setString("fs.s3a.access.key", System.getenv("AWS_ACCESS_KEY"));
        config.setString("fs.s3a.secret.key", System.getenv("AWS_SECRET_KEY"));
        config.setString("fs.s3a.endpoint", FlinkStreamingConfig.getS3EndPoint()); // Use HTTP, not HTTPS
        config.setString("fs.s3a.path.style.access", "true");

        env.configure(config);

        DataStream<String> jsonStream = env.fromElements(TelecomPMData.getJsonString());

        // 3️⃣ Convert JSON to POJO
        DataStream<PMKafkaEventPojo> pojoStream = jsonStream.map(new FlinkJsonToPojo());

        DataStream<Row> rowStream = pojoStream.map(new FlinkJsonToRowMapper());


        // Register Paimon catalog (replace with your configurations)
        tableEnv.executeSql(
                "CREATE CATALOG network_performance_catalog WITH (\n" +
                        "  'type' = 'paimon',\n" +
                        "  'warehouse' = 's3a://flinks3integration/paimon-warehouse',\n" +
                        "  'fs.s3a.access.key' = '"+System.getenv("AWS_ACCESS_KEY")+"',\n" +
                        "  'fs.s3a.secret.key' = '"+System.getenv("AWS_SECRET_KEY")+"',\n" +
                        "  'fs.s3a.endpoint' = '"+FlinkStreamingConfig.getS3EndPoint()+"'\n" +
                        ")");

        // Replace with your catalog name
        String catalogName = "network_performance_catalog";
        String databaseName = "pm_database";

        try {
            tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS " + catalogName + "." + databaseName);
            System.out.println("Database created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating database: " + e.getMessage());
        }
        // Use the catalog and database
        tableEnv.executeSql("USE CATALOG "+catalogName);
        tableEnv.executeSql("USE "+databaseName);


        String createTableSQL = "CREATE TABLE IF NOT EXISTS network_performance_flat (\n" +
                "    ts STRING,\n" +
                "    networkElement_id STRING,\n" +
                "    networkElement_name STRING,\n" +
                "    location_latitude DOUBLE,\n" +
                "    location_longitude DOUBLE,\n" +
                "    networkElement_vendor STRING,\n" +
                "    networkElement_technology STRING,\n" +
                "    cellId STRING,\n" +
                "    throughputMbps DOUBLE,\n" +
                "    latencyMs INT,\n" +
                "    packetLossPercentage DOUBLE,\n" +
                "    cpuUsagePercentage DOUBLE,\n" +
                "    memoryUsagePercentage DOUBLE,\n" +
                "    rssi INT,\n" +
                "    sinr DOUBLE\n" +
                ") WITH (\n" +
                "    'bucket' = '4',\n" +
                "    'bucket-key' = 'networkElement_technology',\n" +
                "    'format' = 'parquet'\n" +
                ");";

        tableEnv.executeSql(createTableSQL);
        System.out.println("Table created ");

        // Define Schema explicitly
        Schema schema = Schema.newBuilder()
                .column("timestamp", "STRING")
                .column("networkElement_id", "STRING")
                .column("networkElement_name", "STRING")
                .column("location_latitude", "DOUBLE")
                .column("location_longitude", "DOUBLE")
                .column("networkElement_vendor", "STRING")
                .column("networkElement_technology", "STRING")
                .column("cellId", "STRING")
                .column("throughputMbps", "DOUBLE")
                .column("latencyMs", "INT")
                .column("packetLossPercentage", "DOUBLE")
                .column("cpuUsagePercentage", "DOUBLE")
                .column("memoryUsagePercentage", "DOUBLE")
                .column("rssi", "INT")
                .column("sinr", "DOUBLE")
                .build();


        stableEnv.fromChangelogStream(rowStream);
        //Table pm_temp_table = tableEnv.fromValues(rowStream);
        tableEnv.createTemporaryView("pm_temp_table", stableEnv.fromChangelogStream(rowStream));

        tableEnv.executeSql("INSERT INTO network_performance_flat SELECT * FROM pm_temp_table");



//        String s3PMPath = FlinkStreamingConfig.getS3BasePath()+"/pm-output-path"; // Replace with your S3 bucket path
//
//
//        rowStream
//                .sinkTo(FileSink
//                        .forRowFormat(new Path(s3PMPath), new SimpleStringEncoder<Row>("UTF-8"))
//                        .withBucketCheckInterval(1000)
//                        .withRollingPolicy(DefaultRollingPolicy.builder().build())
//                        .build());




        /*
        // Create a FileSystem object
        FileSystem fs = FileSystem.get(new Path(FlinkStreamingConfig.getS3BasePath()+"/completed-jobs/sample/Azure/").toUri());

        // Listing files in a given bucket
        String directoryPath = FlinkStreamingConfig.getS3BasePath()+"/completed-jobs/sample/Azure/";  // Change this to your bucket path
        Path path = new Path(directoryPath);
        FileStatus[] fileStatuses = fs.listStatus(path);

        for (FileStatus status : fileStatuses) {
            System.out.println(status.getPath());
        }
*/
        // Your Flink job
        env.fromElements("Hello", "Flink", "World")
                .map(value -> value + " processed")
                .print();  // Print the transformed elements



        // Execute the job
        env.execute("Flink Local Job");
    }
}