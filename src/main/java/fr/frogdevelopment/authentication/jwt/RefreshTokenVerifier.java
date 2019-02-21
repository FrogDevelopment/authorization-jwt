package fr.frogdevelopment.authentication.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenVerifier {

    public void verify(Claims claims) {
        verifyRevokedToken();
    }

    private void verifyRevokedToken() {
//        fixme implement revoked tokens checking (by logout)
//        claims.getId()
//        throw new org.springframework.security.authentication.CredentialsExpiredException("");
    }


}
