package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthTestHelper {
    public static String createTestServerToken(String username) throws CustomInternalServerError {
        return createTestServerToken(username, false);
    }

    public static String createTestServerToken(String username, boolean forAdmin) throws CustomInternalServerError {
        String[] authorities = forAdmin ? new String[2] : new String[1];
        authorities[0] = new SimpleGrantedAuthority("USER").toString();
        if (forAdmin)
            authorities[1] = new SimpleGrantedAuthority("ADMIN").toString();

        return ServerTokenHelper.generateToken(username, authorities);
    }

    public static String createTestBearerToken(String username) throws CustomInternalServerError {
        return ClientTokenHelper.generateToken(username);
    }
}
