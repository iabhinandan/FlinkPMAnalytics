package org.abhinandan.flink;


import org.abhinandan.config.FlinkStreamingConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;
import org.apache.flink.table.catalog.ResolvedCatalogBaseTable;

public class PaimonMainDemo {
    
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().inStreamingMode().build();
        TableEnvironment tableEnv = TableEnvironment.create(settings);

        // Register Paimon catalog (replace with your configurations)
        tableEnv.executeSql(
                "CREATE CATALOG my_paimon_catalog WITH (\n" +
                        "  'type' = 'paimon',\n" +
                        "  'warehouse' = 's3a://flinks3integration/paimon-warehouse',\n" +
                        "  'fs.s3a.access.key' = '"+System.getenv("AWS_ACCESS_KEY")+"',\n" +
                        "  'fs.s3a.secret.key' = '"+System.getenv("AWS_SECRET_KEY")+"',\n" +
                        "  'fs.s3a.endpoint' = '"+FlinkStreamingConfig.getS3EndPoint()+"'\n" +
                        ")");

        // Replace with your catalog name
        String catalogName = "my_paimon_catalog";
        String databaseName = "default_database";

        try {
            tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS " + catalogName + "." + databaseName);
            System.out.println("Database created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        }

        // Use the catalog and database
        tableEnv.executeSql("USE CATALOG my_paimon_catalog");
        tableEnv.executeSql("USE default_database");

        tableEnv.executeSql(
                "CREATE TABLE IF NOT EXISTS my_paimon_table (\n" +
                        "  id BIGINT,\n" +
                        "  name STRING,\n" +
                        "  age INT\n" +
                        ") WITH (\n" +
                        "  'bucket' = '4',\n" +
                        "  'bucket-key' = 'id',\n" + // Added bucket-key
                        "  'format' = 'parquet'\n" +
                        ")");
        System.out.println("Table created successfully.");

        // Insert data into the table
//        tableEnv.executeSql(
//                "INSERT INTO my_paimon_table VALUES\n" +
//                        "(1, 'Alice', 30),\n" +
//                        "(2, 'Bob', 25),\n" +
//                        "(3, 'Charlie', 35)"
//        );

        System.out.println("Select Statement started.");

        tableEnv.executeSql("select * from  my_paimon_table").print();

        env.execute("Flink Paimon Integration Example");
    }
}
