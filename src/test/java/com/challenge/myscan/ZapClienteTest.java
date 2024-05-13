package com.challenge.myscan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zaproxy.clientapi.core.ClientApiException;

import com.challenge.myscan.controller.ScanController;


public class ZapClienteTest {

    @InjectMocks
    private ScanController scanController;

    @Mock
    private ZapCliente zapClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartScan_Success() throws ClientApiException {
        // Preparar os dados de teste
        List<String> urls = new ArrayList<>();
        urls.add("http://testphp.vulnweb.com");
        // urls.add("http://example.org");

        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("urls", urls);

        String scanResult = "Scan finalizado em: http://testphp.vulnweb.com";
                        //   + "Scan finalizado em: http://example.org - ID 456\n";

        // Configurar o comportamento do mock
        when(zapClient.startScan("http://testphp.vulnweb.com")).thenReturn("Scan finalizado em: http://testphp.vulnweb.com");
        // when(zapClient.startScan("http://example.org")).thenReturn("Scan finalizado em: http://example.org - ID 456");

        // Executar o método a ser testado
        ResponseEntity<String> responseEntity = scanController.startScan(requestBody);

        // Verificar se o resultado esperado foi retornado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(scanResult, responseEntity.getBody().trim());
    }

    
    // @Test
    // void testStartScan_Exception() throws ClientApiException {
    //     // Preparar os dados de teste
    //     List<String> urls = new ArrayList<>();
    //     urls.add("http://testphp.vulnweb.cosm/");
        
    //     Map<String, List<String>> requestBody = new HashMap<>();
    //     requestBody.put("urls", urls);
        
    //     // Configurar o comportamento do mock para retornar uma resposta que indica um erro
    //     when(zapClient.startScan("http://testphp.vulnwsadeb.com/")).thenReturn("Error: Erro ao iniciar a varredura");
        
    //     // Executar o método a ser testado
    //     ResponseEntity<String> responseEntity = scanController.startScan(requestBody);
        
    //     // Verificar se a resposta esperada foi retornada
    //     assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    //     assertEquals("Error: Erro ao iniciar a varredura", responseEntity.getBody());
    // }

    
    
    

}
