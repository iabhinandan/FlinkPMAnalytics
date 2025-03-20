package org.abhinandan.util;

import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.Arrays;

public class PiamonCatalogUtil {

    public static boolean catalogExists(StreamTableEnvironment tableEnv, String catalogName) {
        return tableEnv.listCatalogs() != null && Arrays.asList(tableEnv.listCatalogs()).contains(catalogName);
    }

}
