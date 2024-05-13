package com.challenge.myscan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.charset.StandardCharsets;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;


@SuppressWarnings("unused")
@SpringBootApplication
public class MyscanApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyscanApplication.class, args);
	}

	

}
