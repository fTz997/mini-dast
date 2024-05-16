package com.challenge.myscan;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.SpringApplication;
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

    private ClientApi clientApi;

    public ZapCliente() throws ClientApiException {
        clientApi = new ClientApi(ZAP_ADDRESS, ZAP_PORT, ZAP_API_KEY);
    }

    public static void main(String[] args) throws ClientApiException {
        SpringApplication.run(MyscanApplication.class, args);
    }

    public ClientApi getClientApi() {
        return clientApi;
    }
    public void startScan(String targetUrl) {
        try {
            String regex = targetUrl + "/.*?/node_modules/.*";

            clientApi.core.setMode("attack");

            clientApi.spider.setOptionParseComments(true);
            clientApi.spider.setOptionProcessForm(true);
            clientApi.spider.setOptionPostForm(true);
            clientApi.spider.setOptionSendRefererHeader(true);
            clientApi.spider.setOptionParseDsStore(true);
            clientApi.spider.setOptionAcceptCookies(true);
            clientApi.spider.setOptionParseRobotsTxt(true);
            clientApi.spider.setOptionParseSitemapXml(true);
            clientApi.spider.setOptionHandleODataParametersVisited(true);
            clientApi.spider.setOptionShowAdvancedDialog(true);
            clientApi.spider.excludeFromScan(regex);
            clientApi.spider.setOptionThreadCount(64);
            clientApi.spider.setOptionRequestWaitTime(2000);
            clientApi.spider.setOptionUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:125.0) Gecko/20100101 Firefox/125.0");
            clientApi.pscan.enableAllScanners();
            clientApi.pscan.enableAllTags();
            clientApi.pscan.scanOnlyInScope();

            ApiResponse response = clientApi.spider.scan(targetUrl, "0", "true", null, "false");
            String scanId = ((ApiResponseElement) response).getValue();

            clientApi.ajaxSpider.setOptionClickElemsOnce(true);
            clientApi.ajaxSpider.setOptionClickDefaultElems(true);
            clientApi.ajaxSpider.setOptionRandomInputs(true);
            clientApi.ajaxSpider.setOptionEventWait(3000);
            clientApi.ajaxSpider.setOptionMaxCrawlDepth(0);
            clientApi.ajaxSpider.scan(targetUrl, "false", null, "true");
           
            addURLToScanTree(targetUrl);       
            performActiveScan(targetUrl, scanId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addURLToScanTree(String targetUrl) throws Exception {
        clientApi.core.accessUrl(targetUrl, "false");
        if(!getUrlsFromScanTree().contains(targetUrl)) throw new RuntimeException(targetUrl +" not added to scan tree, active scan will not be possible");        
    }
    
    public List<String> getUrlsFromScanTree() throws ClientApiException {
        ApiResponse apiResponse = clientApi.core.urls();
        List<ApiResponse> responses = ((ApiResponseList) apiResponse).getItems();
        return responses.stream().map(r -> ((ApiResponseElement) r).getValue()).collect(Collectors.toList());
    }
    
    public void performActiveScan(String targetUrl, String scanId) throws ClientApiException, InterruptedException {
        String scanPolicyName = "St-Ins-Th-Med";
        clientApi.ascan.setOptionHandleAntiCSRFTokens(true);
        clientApi.ascan.setOptionThreadPerHost(16);
        clientApi.ascan.setOptionRescanInAttackMode(true);
        clientApi.ascan.setOptionPromptInAttackMode(true);
        clientApi.ascan.setOptionShowAdvancedDialog(true);
        clientApi.ascan.enableAllScanners(null);
        clientApi.network.setConnectionTimeout("8");
        clientApi.ascan.scan(targetUrl, "true", "false", scanPolicyName, null, null);

        String status = clientApi.ascan.status(scanId).toString();
        while (status != "100") Thread.sleep(60000);

        // String reportName = scanId + targetUrl; 
        // generateReport(targetUrl, reportName);

    }
    
    // private String generateReport(String targetUrl, String name) {
    //     String reportFilename = name + ".json";
    //     try {
    //         clientApi.reports.generate(
    //                 targetUrl,
    //                 "traditional-json",
    //                 "dark",
    //                 null,
    //                 null,
    //                 targetUrl,
    //                 "chart|alertcount|passingrules|instancecount|statistics|alertdetails",
    //                 null,
    //                 null,
    //                 reportFilename,
    //                 null,
    //                 null, // Use the absolute path here
    //                 null
    //         );
    //         return "ok";
    //     } catch (ClientApiException e) {
    //         e.printStackTrace();
    //         return "Error generating report: " + e.getMessage();
    //     }
    // }
    
}
