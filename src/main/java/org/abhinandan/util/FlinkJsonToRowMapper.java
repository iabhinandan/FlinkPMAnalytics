package org.abhinandan.util;

import org.abhinandan.pojo.PMKafkaEventPojo;
import org.apache.flink.types.Row;
import org.apache.flink.api.common.functions.MapFunction;

public class FlinkJsonToRowMapper implements MapFunction<PMKafkaEventPojo, Row> {
    @Override
    public Row map(PMKafkaEventPojo data) throws Exception {
        // Create a Row with fields corresponding to the POJO's fields
  //      Row row = new Row(15); // Total number of fields in PMKafkaEventPojo

//        row.setField(0, data.getTimestamp());
//        row.setField(1, data.getNetworkElementId());
//        row.setField(2, data.getNetworkElementName());
//        row.setField(3, data.getVendor());
//        row.setField(4, data.getTechnology());
//        row.setField(5, data.getLatitude());
//        row.setField(6, data.getLongitude());
//        row.setField(7, data.getCellId());
//        row.setField(8, data.getThroughputMbps());
//        row.setField(9, data.getLatencyMs());
//        row.setField(10, data.getPacketLossPercentage());
//        row.setField(11, data.getCpuUsagePercentage());
//        row.setField(12, data.getMemoryUsagePercentage());
//        row.setField(13, data.getRssi());
//        row.setField(14, data.getSinr());


//        row.setField("pm_event_timestamp", data.getTimestamp());
//        row.setField("networkElement_id", data.getNetworkElementId());
//        row.setField("networkElement_name", data.getNetworkElementName());
//        row.setField("location_latitude", data.getLatitude());
//        row.setField("location_longitude", data.getLongitude());
//        row.setField("networkElement_vendor", data.getVendor());
//        row.setField("networkElement_technology", data.getTechnology());
//        row.setField("cellId", data.getCellId());
//        row.setField("throughputMbps", data.getThroughputMbps());
//        row.setField("latencyMs", data.getLatencyMs());
//        row.setField("packetLossPercentage", data.getPacketLossPercentage());
//        row.setField("cpuUsagePercentage", data.getCpuUsagePercentage());
//        row.setField("memoryUsagePercentage", data.getMemoryUsagePercentage());
//        row.setField("rssi", data.getRssi());
//        row.setField("sinr", data.getSinr());

        // Field 0: pm_event_timestamp
        // Field 1: networkElement_id
        // Field 2: networkElement_name
        // Field 3: location_latitude
        // Field 4: location_longitude
        // Field 5: networkElement_vendor
        // Field 6: networkElement_technology
        // Field 7: cellId
        // Field 8: throughputMbps
        // Field 9: latencyMs
        // Field 10: packetLossPercentage
        // Field 11: cpuUsagePercentage
        // Field 12: memoryUsagePercentage
        // Field 13: rssi
        // Field 14: sinr

        return Row.of(
                data.getTimestamp(),               // Field 0: pm_event_timestamp
                data.getNetworkElementId(),        // Field 1: networkElement_id
                data.getNetworkElementName(),      // Field 2: networkElement_name
                data.getLatitude(),                // Field 3: location_latitude
                data.getLongitude(),               // Field 4: location_longitude
                data.getVendor(),                  // Field 5: networkElement_vendor
                data.getTechnology(),              // Field 6: networkElement_technology
                data.getCellId(),                  // Field 7: cellId
                data.getThroughputMbps(),          // Field 8: throughputMbps
                data.getLatencyMs(),               // Field 9: latencyMs
                data.getPacketLossPercentage(),    // Field 10: packetLossPercentage
                data.getCpuUsagePercentage(),      // Field 11: cpuUsagePercentage
                data.getMemoryUsagePercentage(),   // Field 12: memoryUsagePercentage
                data.getRssi(),                    // Field 13: rssi
                data.getSinr()                     // Field 14: sinr
        );


    }
}
