package io.jenkins.plugins.zerothreatai.models;

public class ScanResponse {
    private int status;
    private String message;
    private String code;
    private int scanStatus;
    private String url;
    private String timeStamp;

    public ScanResponse() {}

    public ScanResponse(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public int getScanStatus() {
        return scanStatus;
    }

    public String getUrl() {
        return url;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setScanStatus(int scanStatus) {
        this.scanStatus = scanStatus;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
