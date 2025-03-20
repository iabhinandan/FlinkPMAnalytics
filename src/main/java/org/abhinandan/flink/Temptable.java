package org.abhinandan.flink;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
public class Temptable {

        public static void main(String[] args) throws Exception {
            // Step 1: Set up the Flink streaming execution environment
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

            // Step 2: Define the Row schema (field names and types)
            String[] fieldNames = new String[]{
                    "pm_event_timestamp", "networkElement_id", "networkElement_name",
                    "location_latitude", "location_longitude", "networkElement_vendor",
                    "networkElement_technology", "cellId", "throughputMbps", "latencyMs",
                    "packetLossPercentage", "cpuUsagePercentage", "memoryUsagePercentage",
                    "rssi", "sinr"
            };

            TypeInformation<?>[] fieldTypes = new TypeInformation<?>[]{
                    TypeInformation.of(String.class), // pm_event_timestamp
                    TypeInformation.of(String.class), // networkElement_id
                    TypeInformation.of(String.class), // networkElement_name
                    TypeInformation.of(Double.class), // location_latitude
                    TypeInformation.of(Double.class), // location_longitude
                    TypeInformation.of(String.class), // networkElement_vendor
                    TypeInformation.of(String.class), // networkElement_technology
                    TypeInformation.of(String.class), // cellId
                    TypeInformation.of(Double.class), // throughputMbps
                    TypeInformation.of(Integer.class), // latencyMs
                    TypeInformation.of(Double.class), // packetLossPercentage
                    TypeInformation.of(Double.class), // cpuUsagePercentage
                    TypeInformation.of(Double.class), // memoryUsagePercentage
                    TypeInformation.of(Integer.class), // rssi
                    TypeInformation.of(Double.class)  // sinr
            };

            RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes, fieldNames);

            // Step 3: Create a DataStream<Row> with explicit schema
            DataStream<Row> rowStream = env.fromElements(
                    Row.of(
                            "2025-03-16T12:30:45Z", "NE12345", "BaseStation_01",
                            37.7749, -122.4194, "Ericsson", "5G", "Cell_5678",
                            125.5, 15, 0.2, 45.3, 67.8, -75, 25.4
                    )
            ).returns(rowTypeInfo); // Assign RowTypeInfo to the DataStream

            // Step 4: Convert the DataStream<Row> to a Table with schema mapping
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

            Table rowTable = tableEnv.fromDataStream(rowStream, schema);

            // Step 5: Register the Table and interact with it using SQL
            tableEnv.createTemporaryView("pm_temp_table", rowTable);

            // Verify the schema
            tableEnv.executeSql("DESCRIBE pm_temp_table").print();

            // Preview data
            tableEnv.executeSql("SELECT * FROM pm_temp_table").print();
        }
    }


