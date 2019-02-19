package fr.frogdevelopment.authentication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenVerifier {

    private final JwtProperties jwtProperties;

    public RefreshTokenVerifier(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public void verify(Claims claims) {
        verifyRefreshAuthorities(claims);
        verifyRevokedToken();
    }

    private void verifyRevokedToken() {
//        fixme implement revoked tokens checking (by logout)
//        claims.getId()
//        throw new org.springframework.security.authentication.CredentialsExpiredException("");
    }

    private void verifyRefreshAuthorities(Claims claims) {
        List<String> authorities = resolveAuthorities(claims);
        if (authorities == null
                || authorities.isEmpty()
                || authorities.stream().noneMatch(jwtProperties.getRolesRefresh()::equals)) {
            throw new JwtException("FIXME");
        }
    }

    List<String> resolveAuthorities(Claims claims) {
        //noinspection unchecked
        return (List<String>) claims.get(jwtProperties.getAuthoritiesKey(), List.class);
    }
}
