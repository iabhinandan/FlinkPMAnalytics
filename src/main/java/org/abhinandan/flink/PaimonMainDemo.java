package org.abhinandan.flink;


import org.abhinandan.config.FlinkStreamingConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;
import org.apache.flink.table.catalog.ResolvedCatalogBaseTable;

import static org.abhinandan.util.PiamonCatalogUtil.catalogExists;

public class PaimonMainDemo {

    private static final String CATALOG_NAME = "network_performance_catalog";


    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().inStreamingMode().build();
        TableEnvironment tableEnv = TableEnvironment.create(settings);
        StreamTableEnvironment stableEnv = StreamTableEnvironment.create(env);

        // Register Paimon catalog (replace with your configurations)
        if (!catalogExists(stableEnv, CATALOG_NAME)) {
            tableEnv.executeSql(
                    "CREATE CATALOG network_performance_catalog WITH (\n" +
                            "  'type' = 'paimon',\n" +
                            "  'warehouse' = 's3a://flinks3integration/paimon-warehouse',\n" +
                            "  'fs.s3a.access.key' = '" + System.getenv("AWS_ACCESS_KEY") + "',\n" +
                            "  'fs.s3a.secret.key' = '" + System.getenv("AWS_SECRET_KEY") + "',\n" +
                            "  'fs.s3a.endpoint' = '" + FlinkStreamingConfig.getS3EndPoint() + "'\n" +
                            ")");
        } else {
        }

        // Replace with your catalog name
        String catalogName = "network_performance_catalog";
        String databaseName = "pm_database";

        try {
            tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS " + catalogName + "." + databaseName);
            System.out.println("Database created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        }

        // Use the catalog and database
        tableEnv.executeSql("USE CATALOG network_performance_catalog");
        tableEnv.executeSql("USE pm_database");

//        tableEnv.executeSql(
//                "CREATE TABLE IF NOT EXISTS my_paimon_table (\n" +
//                        "  id BIGINT,\n" +
//                        "  name STRING,\n" +
//                        "  age INT\n" +
//                        ") WITH (\n" +
//                        "  'bucket' = '4',\n" +
//                        "  'bucket-key' = 'id',\n" + // Added bucket-key
//                        "  'format' = 'parquet'\n" +
//                        ")");
//        System.out.println("Table created successfully.");

        // Insert data into the table
//        tableEnv.executeSql(
//                "INSERT INTO my_paimon_table VALUES\n" +
//                        "(1, 'Alice', 30),\n" +
//                        "(2, 'Bob', 25),\n" +
//                        "(3, 'Charlie', 35)"
//        );

        System.out.println("Select Statement started.");

        tableEnv.executeSql("select * from  network_performance_flat").print();

        env.execute("Flink Paimon Integration Example");
    }
}
