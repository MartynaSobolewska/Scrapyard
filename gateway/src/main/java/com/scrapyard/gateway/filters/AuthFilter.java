package com.scrapyard.gateway.filters;

import com.scrapyard.gateway.domain.AuthResponseDTO;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RefreshScope
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // TODO: secure /jwt endpoint with a key to ensure only api gateway has an access
        return ((exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                throw new RuntimeException("Missing auth header!");
            }
            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || ! "Bearer".equals(parts[0])){
                throw new RuntimeException("Incorrect auth structure!");
            }
            return webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8080/auth/jwt")
                    .retrieve().bodyToMono(AuthResponseDTO.class)
                    .map(authResponseDTO -> {
                        exchange.getRequest().mutate().header("Authorization", authResponseDTO.getAccessToken());
                        return exchange;
                    }).flatMap(chain::filter);
        });
    }

    public static class Config {

    }

}
