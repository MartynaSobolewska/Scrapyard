package com.example.scrapyard.auth;

import org.springframework.beans.factory.annotation.Value;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 7000000;
    @Value(value = "${security.secrets.server}")
    public static String SERVER_TOKEN_SECRET;
    @Value(value = "${security.secrets.client}")
    public static String CLIENT_TOKEN_SECRET;

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }
}
