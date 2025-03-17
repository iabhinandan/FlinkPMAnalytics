package org.abhinandan.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PMKafkaEventPojo implements Serializable {

    private String timestamp;
    private String networkElementId;
    private String networkElementName;
    private String vendor;
    private String technology;
    private double latitude;
    private double longitude;
    private String cellId;
    private double throughputMbps;
    private int latencyMs;
    private double packetLossPercentage;
    private double cpuUsagePercentage;
    private double memoryUsagePercentage;
    private int rssi;
    private double sinr;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNetworkElementId() {
        return networkElementId;
    }

    public void setNetworkElementId(String networkElementId) {
        this.networkElementId = networkElementId;
    }

    public String getNetworkElementName() {
        return networkElementName;
    }

    public void setNetworkElementName(String networkElementName) {
        this.networkElementName = networkElementName;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    @Override
    public String toString() {
        return "TelecomPMDataFlattened{" +
                "timestamp='" + timestamp + '\'' +
                ", networkElementId='" + networkElementId + '\'' +
                ", networkElementName='" + networkElementName + '\'' +
                ", vendor='" + vendor + '\'' +
                ", technology='" + technology + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", cellId='" + cellId + '\'' +
                ", throughputMbps=" + throughputMbps +
                ", latencyMs=" + latencyMs +
                ", packetLossPercentage=" + packetLossPercentage +
                ", cpuUsagePercentage=" + cpuUsagePercentage +
                ", memoryUsagePercentage=" + memoryUsagePercentage +
                ", rssi=" + rssi +
                ", sinr=" + sinr +
                '}';
    }

}
