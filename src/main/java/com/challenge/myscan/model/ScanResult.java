package com.challenge.myscan.model;

public class ScanResult {
    private String scanId;
    private String status;

	public String getScanId() {
        return scanId;
    }
        
    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public String getStatus() {
        return status;
    }
        
    public void setStatus(String status) {
        this.status= status;
    }

}