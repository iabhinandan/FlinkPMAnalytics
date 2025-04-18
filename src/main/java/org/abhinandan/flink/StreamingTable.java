package org.abhinandan.flink;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.abhinandan.config.FlinkStreamingConfig;
import org.abhinandan.pojo.NetworkPerformanceData;
import org.abhinandan.pojo.PMKafkaEventPojo;
import org.abhinandan.sampledata.TelecomPMData;
import org.abhinandan.util.FlinkJsonToPojo;
import org.abhinandan.util.FlinkJsonToRowMapper;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.hadoop.util.ShutdownHookManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.SysexMessage;
import java.time.Instant;

import static org.abhinandan.util.PiamonCatalogUtil.catalogExists;

public class StreamingTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingTable.class);
    private static final String CATALOG_NAME = "network_performance_catalog";

    private static final String INFLUX_URL = "http://localhost:8086";
    private static final String INFLUX_TOKEN = FlinkStreamingConfig.getInfluxDbToken();
    private static final String ORG = "myorg";
    private static final String BUCKET = "pmdata";


    // Ensure to close the client on shutdown

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Flink job...");

        // Step 1: Set up the streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // For local testing

        // Configure AWS S3 access
        Configuration config = new Configuration();
        config.setString("fs.s3a.access.key", System.getenv("AWS_ACCESS_KEY"));
        config.setString("fs.s3a.secret.key", System.getenv("AWS_SECRET_KEY"));
        config.setString("fs.s3a.endpoint", FlinkStreamingConfig.getS3EndPoint()); // Use HTTP, not HTTPS
        config.setString("fs.s3a.path.style.access", "true");
        env.configure(config);
        // Enable checkpointing
        env.enableCheckpointing(60000); // Checkpoint every 60 seconds (adjust as needed)
        // Configure advanced checkpoint settings
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE); // Ensure exactly-once semantics
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(30000); // Minimum pause between checkpoints
        env.getCheckpointConfig().setCheckpointTimeout(10000); // Checkpoint must complete within 10 seconds
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1); // Allow only one checkpoint to be in progress at a time
        env.getCheckpointConfig().setCheckpointStorage("s3://flinks3integration/flink-checkpoint");

        EnvironmentSettings settings = EnvironmentSettings.newInstance().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);



        // Step 2: Create a DataStream of JSON events (replace with actual stream)

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092") // Kafka broker
                .setTopics("pm_data_topic") // Kafka topic to consume
                .setGroupId("my_consumer_group") // Consumer group
                .setStartingOffsets(OffsetsInitializer.latest()) // Start consuming from the beginning
                .setValueOnlyDeserializer(new SimpleStringSchema()) // Deserializer for message values
                .build();

        // Read from Kafka source
        DataStream<String> jsonStream = env.fromSource(
                kafkaSource,
                org.apache.flink.api.common.eventtime.WatermarkStrategy.noWatermarks(),
                "Kafka Source").rebalance();

        jsonStream.print();
//        DataStream<String> jsonStream = env.fromElements(TelecomPMData.getJsonString());
        LOGGER.info("JSON stream created.");

        // Convert JSON to POJO
        DataStream<PMKafkaEventPojo> pojoStream = jsonStream.map(new FlinkJsonToPojo());
        LOGGER.info("POJO stream created.");

        // Step 3: Define the Row schema
        String[] fieldNames = new String[]{
                "pm_event_timestamp", "networkElement_id", "networkElement_name",
                "location_latitude", "location_longitude", "networkElement_vendor",
                "networkElement_technology", "cellId", "throughputMbps", "latencyMs",
                "packetLossPercentage", "cpuUsagePercentage", "memoryUsagePercentage",
                "rssi", "sinr"
        };

        TypeInformation<?>[] fieldTypes = new TypeInformation<?>[]{
                TypeInformation.of(String.class),
                TypeInformation.of(String.class),
                TypeInformation.of(String.class),
                TypeInformation.of(Double.class),
                TypeInformation.of(Double.class),
                TypeInformation.of(String.class),
                TypeInformation.of(String.class),
                TypeInformation.of(String.class),
                TypeInformation.of(Double.class),
                TypeInformation.of(Integer.class),
                TypeInformation.of(Double.class),
                TypeInformation.of(Double.class),
                TypeInformation.of(Double.class),
                TypeInformation.of(Integer.class),
                TypeInformation.of(Double.class)
        };

        RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes, fieldNames);

        // Convert POJO to Row
        DataStream<Row> rowStream = pojoStream.map(new FlinkJsonToRowMapper()).returns(rowTypeInfo);
        LOGGER.info("Row stream created.");

        // Step 4: Define schema explicitly for Table API
        Schema schema = Schema.newBuilder()
                .column("pm_event_timestamp", "STRING")
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


        //Flink Influx DB

        // Convert Row stream to NetworkPerformanceData
        DataStream<NetworkPerformanceData> inputDataStream = rowStream.map(new MapFunction<Row, NetworkPerformanceData>() {
            @Override
            public NetworkPerformanceData map(Row row) throws Exception {
                return new NetworkPerformanceData(
                        row.getField(1) != null ? row.getField(1).toString() : "unknown",  // networkElementId
                        row.getField(5) != null ? row.getField(5).toString() : "unknown",  // vendor
                        row.getField(6) != null ? row.getField(6).toString() : "unknown",  // technology
                        row.getField(7) != null ? row.getField(7).toString() : "unknown",  // cellId
                        row.getField(8) != null ? (Double) row.getField(8) : 0.0,          // throughputMbps
                        row.getField(9) != null ? (Integer) row.getField(9) : 0,           // latencyMs
                        row.getField(10) != null ? (Double) row.getField(10) : 0.0,        // packetLossPercentage
                        row.getField(11) != null ? (Double) row.getField(11) : 0.0,        // cpuUsagePercentage
                        row.getField(12) != null ? (Double) row.getField(12) : 0.0,        // memoryUsagePercentage
                        row.getField(13) != null ? (Integer) row.getField(13) : 0,         // rssi
                        row.getField(14) != null ? (Double) row.getField(14) : 0.0,        // sinr
                        row.getField(0) != null ? row.getField(0).toString() : Instant.now().toString() // timestamp
                );
            }
        });

        // Send data to InfluxDB
        inputDataStream.map(new MapFunction<NetworkPerformanceData, NetworkPerformanceData>() {
            private transient InfluxDBClient influxDBClient;

            @Override
            public NetworkPerformanceData map(NetworkPerformanceData data) throws Exception {
                if (influxDBClient == null) {
                    // Initialize InfluxDB client
                    influxDBClient = InfluxDBClientFactory.create(INFLUX_URL, INFLUX_TOKEN.toCharArray());
                }

                // Prepare the InfluxDB Point object with all relevant tags and fields
                Point point = Point.measurement("network_performance")
                        .addTag("networkElementId", data.getNetworkElementId())                  // Tag for network element ID
                        .addTag("vendor", data.getVendor())                                      // Tag for vendor (e.g., Nokia/Ericsson)
                        .addTag("technology", data.getTechnology())                              // Tag for technology (e.g., 5G, 4G)
                        .addTag("cellId", data.getCellId())                                      // Tag for cell ID
                        .addField("throughputMbps", data.getThroughputMbps())               // Field for throughput in Mbps
                        .addField("latencyMs", data.getLatencyMs())                         // Field for latency in milliseconds
                        .addField("packetLossPercentage", data.getPacketLossPercentage())   // Field for packet loss percentage
                        .addField("cpuUsagePercentage", data.getCpuUsagePercentage())       // Field for CPU usage percentage
                        .addField("memoryUsagePercentage", data.getMemoryUsagePercentage()) // Field for memory usage percentage
                        .addField("rssi", data.getRssi())                                   // Field for RSSI
                        .addField("sinr", data.getSinr())                                   // Field for SINR
                        .time(Instant.parse(data.getTimestamp()), WritePrecision.MS);         // Timestamp in ISO format (milliseconds)

                // Write the point to InfluxDB
                influxDBClient.getWriteApiBlocking().writePoint(BUCKET, ORG, point);

                // Log the data being written for debugging purposes
                LOGGER.info("Writing to InfluxDB - ElementId: {}, Vendor: {}, Technology: {}, CellId: {}, Throughput: {}, Latency: {}, "
                                + "Packet Loss: {}, CPU Usage: {}, Memory Usage: {}, RSSI: {}, SINR: {}, Timestamp: {}",
                        data.getNetworkElementId(), data.getVendor(), data.getTechnology(), data.getCellId(), data.getThroughputMbps(), data.getLatencyMs(),
                        data.getPacketLossPercentage(), data.getCpuUsagePercentage(),
                        data.getMemoryUsagePercentage(), data.getRssi(), data.getSinr(), data.getTimestamp());

                System.out.println(String.format(
                        "Data written - NetworkElementId: %s, Vendor: %s, Technology: %s, CellId: %s, Throughput: %.2f, Latency: %d, "
                                + "PacketLossPercentage: %.2f, CPUUsagePercentage: %.2f, MemoryUsagePercentage: %.2f, RSSI: %d, SINR: %.2f, Timestamp: %s",
                        data.getNetworkElementId(), data.getVendor(), data.getTechnology(), data.getCellId(), data.getThroughputMbps(), data.getLatencyMs(),
                        data.getPacketLossPercentage(), data.getCpuUsagePercentage(),
                        data.getMemoryUsagePercentage(), data.getRssi(), data.getSinr(), data.getTimestamp()));

                // Return the data object for further processing in the stream
                return data;
            }

            @Override
            protected void finalize() throws Throwable {
                if (influxDBClient != null) {
                    influxDBClient.close();
                }
            }
        });

        //CLose

        // Ensure catalog creation
        if (!catalogExists(tableEnv, CATALOG_NAME)) {
            tableEnv.executeSql(
                    "CREATE CATALOG network_performance_catalog WITH (\n" +
                            "  'type' = 'paimon',\n" +
                            "  'warehouse' = 's3a://flinks3integration/paimon-warehouse',\n" +
                            "  'fs.s3a.access.key' = '" + System.getenv("AWS_ACCESS_KEY") + "',\n" +
                            "  'fs.s3a.secret.key' = '" + System.getenv("AWS_SECRET_KEY") + "',\n" +
                            "  'fs.s3a.endpoint' = '" + FlinkStreamingConfig.getS3EndPoint() + "'\n" +
                            ")");
            LOGGER.info("Catalog '{}' created.", CATALOG_NAME);
        } else {
            LOGGER.info("Catalog '{}' already exists.", CATALOG_NAME);
        }

        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS pm_database");
        LOGGER.info("Database 'pm_database' created or already exists.");

        tableEnv.executeSql("USE CATALOG network_performance_catalog");
        tableEnv.executeSql("USE pm_database");
        //tableEnv.executeSql("drop table  IF EXISTS network_performance_flat");
        tableEnv.executeSql("SHOW TABLES").print();
//        String createTableSQL = "CREATE TABLE IF NOT EXISTS network_performance_flat (\n" +
//                "    pm_event_timestamp STRING,\n" +
//                "    networkElement_id STRING,\n" +
//                "    networkElement_name STRING,\n" +
//                "    location_latitude DOUBLE,\n" +
//                "    location_longitude DOUBLE,\n" +
//                "    networkElement_vendor STRING,\n" +
//                "    networkElement_technology STRING,\n" +
//                "    cellId STRING,\n" +
//                "    throughputMbps DOUBLE,\n" +
//                "    latencyMs INT,\n" +
//                "    packetLossPercentage DOUBLE,\n" +
//                "    cpuUsagePercentage DOUBLE,\n" +
//                "    memoryUsagePercentage DOUBLE,\n" +
//                "    rssi INT,\n" +
//                "    sinr DOUBLE\n" +
//                ") WITH (\n" +
//                "    'bucket' = '4',\n" +
//                "    'bucket-key' = 'networkElement_technology',\n" +
//                "    'format' = 'parquet'\n" +
//                ");";
//        tableEnv.executeSql(createTableSQL);

        tableEnv.executeSql("CREATE TABLE IF NOT EXISTS network_performance_flat (\n" +
                "    pm_event_timestamp STRING,\n" +
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
                "    'log.consistency' = 'transactional',\n" +  // Ensure proper consistency
                "    'write-mode' = 'change-log',\n" +        // Enable streaming writes
                "    'format' = 'parquet'\n" +               // Specify data format
                ");");


        LOGGER.info("Table 'network_performance_flat' created.");
        tableEnv.executeSql("SHOW TABLES").print();
        // Convert the Row DataStream to a Table
        Table rowTable = tableEnv.fromDataStream(rowStream, schema);
        LOGGER.info("Table created from Row stream.");

        rowTable.executeInsert("network_performance_flat");

//        // Register Table as a Temporary View
//        tableEnv.createTemporaryView("pm_temp_table", rowTable);
//        LOGGER.info("Temporary view 'pm_temp_table' registered.");
//
//        // Debugging: Describe the temporary table
//        LOGGER.info("Describing 'pm_temp_table'...");
//        tableEnv.executeSql("DESCRIBE pm_temp_table").print();
//
//        // Debugging: Select data from the temporary table
//        LOGGER.info("Previewing data from 'pm_temp_table'...");
//        tableEnv.executeSql("SELECT * FROM pm_temp_table").print();
//        // Insert data into the target table
//        LOGGER.info("Inserting data into 'network_performance_flat'...");
//        tableEnv.executeSql(
//                "INSERT INTO network_performance_flat SELECT * FROM pm_temp_table");
//        LOGGER.info("Data successfully inserted into 'network_performance_flat'.");


//        tableEnv.executeSql(
//                "SELECT * FROM network_performance_flat").print();

        env.execute("Flink Local Paimon Data lake Job");
        //Runtime.getRuntime().addShutdownHook(new Thread(() -> influxDBClient.close()));

        //ShutdownHookManager.get().clearShutdownHooks();
    }

}
