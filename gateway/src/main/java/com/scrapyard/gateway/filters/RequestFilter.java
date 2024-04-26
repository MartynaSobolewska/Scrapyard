package com.scrapyard.gateway.filters;

import com.scrapyard.gateway.domain.CarDTO;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Object body = exchange.getAttribute("cachedRequestBodyObject");
        System.out.println("Request Filter Called");
        if (body instanceof CarDTO){
            CarDTO carDTO = (CarDTO) body;
            System.out.println("Car Details: " + carDTO);
        }
        return chain.filter(exchange);
    }
}
