package fr.frogdevelopment.authentication.jwt;

import io.jsonwebtoken.Claims;
import java.util.Objects;
import org.springframework.security.authentication.CredentialsExpiredException;

class RefreshTokenVerifier {

    private final JwtUserDetailsService jwtUserDetailsService;

    RefreshTokenVerifier(JwtUserDetailsService jwtUserDetailsService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    void verify(Claims claims) {
        verifyRevokedToken(claims.getId());
    }

    private void verifyRevokedToken(String jti) {
        Objects.requireNonNull(jti, "Required jti !!!");

        boolean isRevoked = jwtUserDetailsService.isRevoked(jti);
        if (isRevoked) {
            throw new CredentialsExpiredException("Token has been revoked !!");
        }
    }

}
