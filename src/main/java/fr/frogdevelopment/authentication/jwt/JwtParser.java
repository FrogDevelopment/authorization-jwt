package fr.frogdevelopment.authentication.jwt;

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

    private final JwtProperties jwtProperties;
    private final RefreshTokenVerifier refreshTokenVerifier;
    private final RequestParser requestParser;

    @Autowired
    public JwtParser(JwtProperties jwtProperties,
                     @Autowired(required = false) RefreshTokenVerifier refreshTokenVerifier,
                     RequestParser requestParser) {
        this.jwtProperties = jwtProperties;
        this.refreshTokenVerifier = refreshTokenVerifier;
        this.requestParser = requestParser;
    }

    private Claims resolveClaims(@NotNull String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    @Nullable
    public String getUsernameFromToken(@NotNull HttpServletRequest request) {
        var token = requestParser.retrieveToken(request);
        if (token == null) {
            return null;
        }

        return resolveClaims(token).getSubject();
    }

    @Nullable
    public String getIdFromRefreshToken(@NotNull HttpServletRequest request) {
        var token = requestParser.retrieveToken(request);
        if (token == null) {
            return null;
        }

        return resolveClaims(token).getId();
    }

    @Nullable
    public String getUsernameFromRefreshToken(@NotNull HttpServletRequest request) {
        var token = requestParser.retrieveToken(request);
        if (token == null) {
            return null;
        }

        var claims = resolveClaims(token);

        if (refreshTokenVerifier != null) {
            refreshTokenVerifier.verify(claims);
        }

        return claims.getSubject();
    }

    @Nullable
    public Authentication createAuthentication(@NotNull HttpServletRequest request) {
        var token = requestParser.retrieveToken(request);
        if (token == null) {
            return null;
        }

        var claims = resolveClaims(token);

        var username = claims.getSubject();

        var grantedAuthorities = resolveAuthorities(claims);

        return new JwtAuthenticationToken(username, grantedAuthorities);
    }

    private List<SimpleGrantedAuthority> resolveAuthorities(@NotNull Claims claims) {
        //noinspection unchecked
        return ((List<String>) claims.get(TokenProvider.AUTHORITIES_KEY, List.class))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
