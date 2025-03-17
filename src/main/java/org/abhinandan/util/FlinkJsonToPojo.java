package org.abhinandan.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.abhinandan.pojo.PMKafkaEventPojo;
import org.apache.flink.api.common.functions.MapFunction;

public class FlinkJsonToPojo implements MapFunction<String, PMKafkaEventPojo> {

    private static final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

    @Override
    public PMKafkaEventPojo map(String json) throws Exception {
        // Parse the JSON string into a JsonNode object
        JsonNode node = objectMapper.readTree(json);

        // Create a new TelecomPMDataFlattened object
        PMKafkaEventPojo data = new PMKafkaEventPojo();

        data.setTimestamp(node.get("timestamp").asText());

        JsonNode ne = node.get("networkElement");
        data.setNetworkElementId(ne.get("id").asText());
        data.setNetworkElementName(ne.get("name").asText());
        data.setVendor(ne.get("vendor").asText());
        data.setTechnology(ne.get("technology").asText());

        JsonNode location = ne.get("location");
        data.setLatitude(location.get("latitude").asDouble());
        data.setLongitude(location.get("longitude").asDouble());

        JsonNode metrics = node.get("performanceMetrics");
        data.setCellId(metrics.get("cellId").asText());
        data.setThroughputMbps(metrics.get("throughputMbps").asDouble());
        data.setLatencyMs(metrics.get("latencyMs").asInt());
        data.setPacketLossPercentage(metrics.get("packetLossPercentage").asDouble());
        data.setCpuUsagePercentage(metrics.get("cpuUsagePercentage").asDouble());
        data.setMemoryUsagePercentage(metrics.get("memoryUsagePercentage").asDouble());
        data.setRssi(metrics.get("rssi").asInt());
        data.setSinr(metrics.get("sinr").asDouble());

        // Return the populated POJO
        return data;
    }
}