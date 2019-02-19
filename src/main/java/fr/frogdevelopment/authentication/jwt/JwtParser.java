package fr.frogdevelopment.authentication.jwt;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtParser {

    static final String TOKEN_TYPE = "Bearer ";

    private final JwtProperties jwtProperties;
    private final RefreshTokenVerifier refreshTokenVerifier;

    @Autowired
    public JwtParser(JwtProperties jwtProperties,
                     RefreshTokenVerifier refreshTokenVerifier) {
        this.jwtProperties = jwtProperties;
        this.refreshTokenVerifier = refreshTokenVerifier;
    }

    private String retrieveToken(@NotNull HttpServletRequest request) {
        var bearer = request.getHeader(AUTHORIZATION);
        if (bearer == null || !bearer.startsWith(TOKEN_TYPE)) {
            return null;
        }

        return bearer.replace(TOKEN_TYPE, "");
    }

    private Claims resolveClaims(@NotNull String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    @Nullable
    public String resolveName(@NotNull HttpServletRequest request) {
        var token = retrieveToken(request);
        if (token == null) {
            return null;
        }

        return resolveClaims(token).getSubject();
    }

    @Nullable
    public String refreshToken(@NotNull HttpServletRequest request) {
        var token = retrieveToken(request);
        if (token == null) {
            return null;
        }

        var claims = resolveClaims(token);

        refreshTokenVerifier.verify(claims);

        return claims.getSubject();
    }

    @Nullable
    public Authentication createAuthentication(@NotNull HttpServletRequest request) {
        var token = retrieveToken(request);
        if (token == null) {
            return null;
        }

        var claims = resolveClaims(token);

        var username = claims.getSubject();

        var grantedAuthorities = resolveAuthorities(claims).stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new JwtAuthenticationToken(username, grantedAuthorities);
    }

    private List<String> resolveAuthorities(@NotNull Claims claims) {
        //noinspection unchecked
        return (List<String>) claims.get(jwtProperties.getAuthoritiesKey(), List.class);
    }
}
