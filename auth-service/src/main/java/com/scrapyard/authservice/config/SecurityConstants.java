package com.scrapyard.authservice.config;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 7000000;
    public static final String SERVER_TOKEN_SECRET = "secret1";
    public static final String CLIENT_TOKEN_SECRET = "secret";

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }
}
