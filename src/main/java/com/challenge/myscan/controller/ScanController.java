package com.challenge.myscan.controller;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zaproxy.clientapi.core.ClientApiException;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;

import com.challenge.myscan.ZapCliente;

@RestController
public class ScanController {

    @Autowired
    private ZapCliente zapClient;

    private ExecutorService executorService = Executors.newFixedThreadPool(5); 

    @PostMapping("/scan")
    public ResponseEntity<String> startScan(@RequestBody Map<String, List<String>> requestBody) throws ClientApiException {
        List<String> urls = requestBody.get("urls");

        StringBuilder responseBuilder = new StringBuilder();

        for (String url : urls) {
            executorService.submit(() -> zapClient.startScan(url));

            responseBuilder.append("Scan iniciado, o tempo médio de espera é de 15 minutos por host ").append(url).append("\n");
        }

        executorService.shutdown();

        return ResponseEntity.ok(responseBuilder.toString());
    }

    @GetMapping("/scan")
    public ResponseEntity<?> getActiveScans() {
        try {
            ApiResponseList scans = (ApiResponseList) zapClient.getClientApi().ascan.scans();
            
            List<Collection<ApiResponse>> activeScans = scans.getItems().stream()
                .filter(item -> item instanceof ApiResponseSet)
                .map(item -> (ApiResponseSet) item)
                .map(scan -> scan.getValues())
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(activeScans);
        } catch (ClientApiException e) {
            return ResponseEntity.status(500).body("Error retrieving active scans: " + e.getMessage());
        }
    }
}
