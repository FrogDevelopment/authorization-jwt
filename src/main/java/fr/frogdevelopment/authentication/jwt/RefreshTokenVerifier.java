package fr.frogdevelopment.authentication.jwt;

import io.jsonwebtoken.Claims;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
class RefreshTokenVerifier {

    void verify(Claims claims) {
        verifyRevokedToken(claims.getId());
    }

    private void verifyRevokedToken(String jti) {
        Objects.requireNonNull(jti, "Required id !!!");
//        fixme implement revoked tokens checking (by logout)
//        throw new org.springframework.security.authentication.CredentialsExpiredException("");
    }


}
