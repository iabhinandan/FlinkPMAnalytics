package org.abhinandan.util;

import org.abhinandan.pojo.PMKafkaEventPojo;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.types.Row;
import org.apache.flink.api.common.functions.MapFunction;

import org.abhinandan.pojo.PMKafkaEventPojo;
import org.apache.flink.types.Row;
import org.apache.flink.api.common.functions.MapFunction;

public class FlinkJsonToRowMapper implements MapFunction<PMKafkaEventPojo, Row> {
    @Override
    public Row map(PMKafkaEventPojo data) throws Exception {
        // Create a Row with fields corresponding to the POJO's fields
        Row row = new Row(15); // Total number of fields in PMKafkaEventPojo

        row.setField(0, data.getTimestamp());
        row.setField(1, data.getNetworkElementId());
        row.setField(2, data.getNetworkElementName());
        row.setField(3, data.getVendor());
        row.setField(4, data.getTechnology());
        row.setField(5, data.getLatitude());
        row.setField(6, data.getLongitude());
        row.setField(7, data.getCellId());
        row.setField(8, data.getThroughputMbps());
        row.setField(9, data.getLatencyMs());
        row.setField(10, data.getPacketLossPercentage());
        row.setField(11, data.getCpuUsagePercentage());
        row.setField(12, data.getMemoryUsagePercentage());
        row.setField(13, data.getRssi());
        row.setField(14, data.getSinr());




        return row;
    }
}
