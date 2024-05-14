package com.example.scrapyard.auth;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 7000000;
    public static final String JWT_SECRET = "secret1";
    public static final String BEARER_SECRET = "secret";

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }
}
