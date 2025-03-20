package org.abhinandan.pojo;

public class NetworkPerformanceData {

    private String networkElementId;
    private String vendor;
    private String technology; // Added technology field for completeness
    private String cellId; // Added cell ID for more detailed information
    private double throughputMbps;
    private int latencyMs;
    private double packetLossPercentage;
    private double cpuUsagePercentage;
    private double memoryUsagePercentage;
    private int rssi;
    private double sinr;
    private String timestamp;

    // Default constructor
    public NetworkPerformanceData() {}

    // Parameterized constructor for all fields
    public NetworkPerformanceData(String networkElementId, String vendor, String technology, String cellId,
                                  double throughputMbps, int latencyMs, double packetLossPercentage,
                                  double cpuUsagePercentage, double memoryUsagePercentage, int rssi,
                                  double sinr, String timestamp) {
        this.networkElementId = networkElementId;
        this.vendor = vendor;
        this.technology = technology;
        this.cellId = cellId;
        this.throughputMbps = throughputMbps;
        this.latencyMs = latencyMs;
        this.packetLossPercentage = packetLossPercentage;
        this.cpuUsagePercentage = cpuUsagePercentage;
        this.memoryUsagePercentage = memoryUsagePercentage;
        this.rssi = rssi;
        this.sinr = sinr;
        this.timestamp = timestamp;
    }

    // Getter and Setter methods for each field
    public String getNetworkElementId() {
        return networkElementId;
    }

    public void setNetworkElementId(String networkElementId) {
        this.networkElementId = networkElementId;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public double getThroughputMbps() {
        return throughputMbps;
    }

    public void setThroughputMbps(double throughputMbps) {
        this.throughputMbps = throughputMbps;
    }

    public int getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(int latencyMs) {
        this.latencyMs = latencyMs;
    }

    public double getPacketLossPercentage() {
        return packetLossPercentage;
    }

    public void setPacketLossPercentage(double packetLossPercentage) {
        this.packetLossPercentage = packetLossPercentage;
    }

    public double getCpuUsagePercentage() {
        return cpuUsagePercentage;
    }

    public void setCpuUsagePercentage(double cpuUsagePercentage) {
        this.cpuUsagePercentage = cpuUsagePercentage;
    }

    public double getMemoryUsagePercentage() {
        return memoryUsagePercentage;
    }

    public void setMemoryUsagePercentage(double memoryUsagePercentage) {
        this.memoryUsagePercentage = memoryUsagePercentage;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public double getSinr() {
        return sinr;
    }

    public void setSinr(double sinr) {
        this.sinr = sinr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Override toString() method for better debugging
    @Override
    public String toString() {
        return "NetworkPerformanceData{" +
                "networkElementId='" + networkElementId + '\'' +
                ", vendor='" + vendor + '\'' +
                ", technology='" + technology + '\'' +
                ", cellId='" + cellId + '\'' +
                ", throughputMbps=" + throughputMbps +
                ", latencyMs=" + latencyMs +
                ", packetLossPercentage=" + packetLossPercentage +
                ", cpuUsagePercentage=" + cpuUsagePercentage +
                ", memoryUsagePercentage=" + memoryUsagePercentage +
                ", rssi=" + rssi +
                ", sinr=" + sinr +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
