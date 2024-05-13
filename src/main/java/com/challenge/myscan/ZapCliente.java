package com.challenge.myscan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;

@Component
public class ZapCliente {

    private static final String ZAP_ADDRESS = "localhost";
    private static final int ZAP_PORT = 8090;
    private static final String ZAP_API_KEY = null; // Adicione sua chave API do ZAP aqui

    @Autowired
    private Environment env;

    private ClientApi clientApi;

    public ZapCliente() throws ClientApiException {
        clientApi = new ClientApi(ZAP_ADDRESS, ZAP_PORT, ZAP_API_KEY);
    }

    public static void main(String[] args) throws ClientApiException {
        SpringApplication.run(MyscanApplication.class, args);
    }

    public String startScan(String targetUrl) {
        try {
            String maxChildren = "0";
            String recurse = "true";
            String subTree = "true";
                        
            clientApi.core.setMode("attack");
    
            addURLToScanTree(targetUrl);
    
            clientApi.spider.setOptionParseComments(true);
            clientApi.spider.setOptionPostForm(true);
            clientApi.spider.setOptionProcessForm(true);
            clientApi.spider.setOptionSendRefererHeader(true);
            clientApi.spider.setOptionParseDsStore(true);
            clientApi.spider.setOptionAcceptCookies(true);
            clientApi.spider.setOptionParseRobotsTxt(true);
            clientApi.spider.setOptionParseSitemapXml(true);
            clientApi.spider.setOptionRequestWaitTime(2500);
            clientApi.spider.excludeFromScan("^(?!.*//node_modules//).*$");
            clientApi.spider.setOptionUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:125.0) Gecko/20100101 Firefox/125.0");
            clientApi.pscan.scanOnlyInScope();
            clientApi.pscan.enableAllScanners();
            clientApi.pscan.enableAllTags();
            ApiResponse response = clientApi.spider.scan(targetUrl, maxChildren, recurse, null, subTree);
    
            String scanId = ((ApiResponseElement) response).getValue();
    
            performActiveScan(targetUrl);
    
            String report = generateReport(targetUrl, "Report");

            // saveReportToDatabase(report);
            return "Scan finalizado em: " + targetUrl + " - ID "+ scanId.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao iniciar a varredura: " + e.getMessage();
        }
        
    }
    
    public void addURLToScanTree(String targetUrl) throws Exception {
        try {
            URL obj = new URL("http://"+ZAP_ADDRESS+":"+ZAP_PORT+"/JSON/core/action/accessUrl/?url=" + targetUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            if (!(responseCode == HttpURLConnection.HTTP_OK)) {
                throw new RuntimeException("Falha ao adicionar URL à árvore de varredura. Código de resposta: " + responseCode);
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao adicionar URL à árvore de varredura: " + e.getMessage());
        }
    }
    
    public List<String> getUrlsFromScanTree() throws ClientApiException {
        ApiResponse apiResponse = clientApi.core.urls();
        List<ApiResponse> responses = ((ApiResponseList) apiResponse).getItems();
        return responses.stream().map(r -> ((ApiResponseElement) r).getValue()).collect(Collectors.toList());
    }
    
    public void performActiveScan(String targetUrl) throws ClientApiException, InterruptedException {
        String scanPolicyName = "St-Ins-Th-Med";

        clientApi.ascan.setOptionHandleAntiCSRFTokens(true);
        clientApi.ascan.setOptionThreadPerHost(15);
        clientApi.ascan.setOptionHostPerScan(10);
        clientApi.ascan.setOptionRescanInAttackMode(true);
        clientApi.ascan.setOptionPromptInAttackMode(true);
        clientApi.ascan.enableAllScanners(null);
    
        clientApi.ascan.scan(targetUrl, "true", "false", scanPolicyName, null, null);
             
    }
    
    private String generateReport(String targetUrl, String name) {
        String reportFilename = name + ".html";

        try {


            clientApi.reports.generate(
                    "Demo Title",
                    "traditional-html-plus",
                    "dark",
                    null,
                    null,
                    targetUrl,
                    "chart|alertcount|passingrules|instancecount|statistics|alertdetails",
                    null,
                    "High|Medium|Low",
                    reportFilename,
                    null,
                    null, // Use the absolute path here
                    "true"
            );
            return "ok";
        } catch (ClientApiException e) {
            e.printStackTrace();
            return "Error generating report: " + e.getMessage();
        }
    }
    
}
