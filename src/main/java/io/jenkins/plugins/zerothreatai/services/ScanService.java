package io.jenkins.plugins.zerothreatai.services;

import hudson.util.Secret;
import io.jenkins.plugins.zerothreatai.models.ScanResponse;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import net.sf.json.JSONObject;

public class ScanService {
    private static final String CLOUD_ENDPOINT = "https://api.zerothreat.ai";
    private static final String ZT_TOKEN_HEADER_KEY = "zt-token";
    private final String baseUrl;

    public ScanService() {
        this.baseUrl = CLOUD_ENDPOINT;
    }

    public ScanService(String baseUrl) {
        if (baseUrl != null && !baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        } else {
            this.baseUrl = CLOUD_ENDPOINT;
        }
    }

    private String ztServerUrl() {
        return this.baseUrl + "/api/scan/devops";
    }

    public ScanResponse initiateScan(Secret token) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(this.ztServerUrl()).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty(ZT_TOKEN_HEADER_KEY, Secret.toString(token));
            conn.setDoOutput(true);

            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A")) {
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                JSONObject jsonResponse = JSONObject.fromObject(response);
                ScanResponse scanResponse = new ScanResponse();
                scanResponse.setStatus(jsonResponse.getInt("status"));
                scanResponse.setMessage(jsonResponse.getString("message"));
                scanResponse.setCode(jsonResponse.getString("code"));
                scanResponse.setScanStatus(jsonResponse.getInt("scanStatus"));
                scanResponse.setUrl(jsonResponse.getString("url"));
                scanResponse.setTimeStamp(jsonResponse.getString("timeStamp"));
                return scanResponse;
            }
        } catch (Exception e) {
            String message = e.getMessage();
            return new ScanResponse(message);
        }
    }

    public boolean pollScanStatus(Secret token, String code, PrintStream logger) {
        int status = 1;
        while (status < 4) {
            try {
                TimeUnit.SECONDS.sleep(300);
                HttpURLConnection conn = (HttpURLConnection) new URL(this.ztServerUrl() + "/" + code).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty(ZT_TOKEN_HEADER_KEY, Secret.toString(token));

                try (Scanner scanner =
                        new Scanner(conn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A"); ) {
                    String response = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();

                    JSONObject jsonResponse = JSONObject.fromObject(response);
                    status = jsonResponse.getInt("scanStatus");
                    var timeStamp = jsonResponse.getString("timeStamp");

                    logger.println("Scan is in progress...   [ " + timeStamp + " ]");
                }

            } catch (Exception e) {
                logger.println("Error polling scan status: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
