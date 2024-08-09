package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthTestHelper {

    final ServerTokenHelper serverTokenHelper;
    final ClientTokenHelper clientTokenHelper;

    public AuthTestHelper(ServerTokenHelper serverTokenHelper, ClientTokenHelper clientTokenHelper) {
        this.serverTokenHelper = serverTokenHelper;
        this.clientTokenHelper = clientTokenHelper;
    }

    public String createTestServerToken(String username) throws CustomInternalServerError {
        return createTestServerToken(username, false);
    }

    public String createTestServerToken(String username, boolean forAdmin) throws CustomInternalServerError {
        String[] authorities = forAdmin ? new String[2] : new String[1];
        authorities[0] = new SimpleGrantedAuthority("USER").toString();
        if (forAdmin)
            authorities[1] = new SimpleGrantedAuthority("ADMIN").toString();

        return serverTokenHelper.generateToken(username, authorities);
    }

    public String createTestBearerToken(String username) throws CustomInternalServerError {
        return clientTokenHelper.generateToken(username);
    }
}
