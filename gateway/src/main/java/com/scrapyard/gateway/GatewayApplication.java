package com.scrapyard.gateway;

import com.netflix.discovery.EurekaClient;
import com.scrapyard.gateway.filters.AuthFilter;
import com.scrapyard.gateway.filters.RequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
	@Autowired
	@Lazy
	private EurekaClient eurekaClient;

	@Autowired
	RequestFilter requestFilter;

	@Autowired
	AuthFilter authFilter;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		// adding 2 rotes to first microservice as we need to log request body if method is POST
		return builder.routes()
				.route("GetBearerToken", r -> r.path("/auth/register")
						.and().method("POST")
						.uri("http://localhost:8083"))
				.route("GetScrapyardCars", r -> r.path("/car")
						.and().method("GET")
						.filters(f -> f.filters(authFilter))
						.uri("http://localhost:8080/car"))
				.build();
	}

}
