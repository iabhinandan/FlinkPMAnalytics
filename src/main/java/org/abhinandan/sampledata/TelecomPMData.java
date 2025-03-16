package org.abhinandan.sampledata;

public class TelecomPMData {
    private static final String JSON_STRING = "{\n" +
            "  \"timestamp\": \"2025-03-16T12:30:45Z\",\n" +
            "  \"networkElement\": {\n" +
            "    \"id\": \"NE12345\",\n" +
            "    \"name\": \"BaseStation_01\",\n" +
            "    \"location\": {\n" +
            "      \"latitude\": 37.7749,\n" +
            "      \"longitude\": -122.4194\n" +
            "    },\n" +
            "    \"vendor\": \"Ericsson\",\n" +
            "    \"technology\": \"5G\"\n" +
            "  },\n" +
            "  \"performanceMetrics\": {\n" +
            "    \"cellId\": \"Cell_5678\",\n" +
            "    \"throughputMbps\": 125.5,\n" +
            "    \"latencyMs\": 15,\n" +
            "    \"packetLossPercentage\": 0.2,\n" +
            "    \"cpuUsagePercentage\": 45.3,\n" +
            "    \"memoryUsagePercentage\": 67.8,\n" +
            "    \"rssi\": -75,\n" +
            "    \"sinr\": 25.4\n" +
            "  }\n" +
            "}";

    public static String getJsonString() {
        return JSON_STRING;
    }

    public static void main(String[] args) {
        System.out.println(getJsonString());
    }
}
