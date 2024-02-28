package com.example.scrapyardservicediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ScrapyardServiceDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrapyardServiceDiscoveryApplication.class, args);
	}

}
