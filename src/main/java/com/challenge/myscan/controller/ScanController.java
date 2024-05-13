package com.challenge.myscan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zaproxy.clientapi.core.ClientApiException;

import com.challenge.myscan.ZapCliente;

@RestController
public class ScanController {

    @Autowired
    private ZapCliente zapClient;

    @PostMapping("/scan")
    public ResponseEntity<String> startScan(@RequestBody Map<String, List<String>> requestBody) throws ClientApiException {
        // Extrair a lista de URLs do corpo da requisição
        List<String> urls = requestBody.get("urls");
    
        StringBuilder scanResults = new StringBuilder();
    
        // Iterar sobre cada URL e iniciar a varredura com o ZAP Proxy
        for (String url : urls) {
            String scanResult = zapClient.startScan(url);
            scanResults.append(scanResult).append("\n");
        }
    
        return ResponseEntity.ok(scanResults.toString());
    }

}