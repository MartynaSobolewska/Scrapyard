package com.scrapyard.gateway.filters;

import com.scrapyard.gateway.domain.AuthResponseDTO;
import com.scrapyard.gateway.domain.LoginBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthFilter implements GatewayFilter {

    final
    RestTemplate restTemplate;

    public AuthFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // TODO: secure /jwt endpoint with a key to ensure only api gateway has an access
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new RuntimeException("Missing auth header!");
        }
        String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        String[] parts = authHeader.split(" ");

        if (parts.length != 2 || !"Bearer".equals(parts[0])) {
            throw new RuntimeException("Incorrect auth structure!");
        }

        Map<String, String> bodyMap = new HashMap();
        bodyMap.put("bearerToken",parts[1]);

        String url = "http://localhost:8083/auth/login";

        // It is blocking, the apis are nonreactive so it doesn't matter
        LoginBody body = LoginBody.builder().bearerToken(parts[1]).build();
        AuthResponseDTO authResponse = restTemplate.postForObject(url, body, AuthResponseDTO.class);
        System.out.println(authResponse);

        // Modify the request to add the new Authorization header
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.getToken())
                .build();
        System.out.println(mutatedRequest);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
