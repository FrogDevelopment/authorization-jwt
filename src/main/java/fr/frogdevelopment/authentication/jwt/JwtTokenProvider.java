package fr.frogdevelopment.authentication.jwt;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    static final String TOKEN_TYPE = "Bearer ";
    static final String CLAIM_NAME = "authorities";

    private final JwtProperties jwtProperties;

    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    String createToken(Authentication authentication) {
        var username = authentication.getName();
        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        var now = new Date();
        var validity = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_NAME, authorities)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();
    }

    String resolveToken(@NotNull HttpServletRequest request) {
        var bearer = request.getHeader(AUTHORIZATION);
        if (bearer == null || !bearer.startsWith(TOKEN_TYPE)) {
            return null;
        }

        return bearer.replace(TOKEN_TYPE, "");
    }

    Claims resolveClaims(@NotNull String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("Unable to parse token", e);
            throw new BadCredentialsException("Expired or invalid JWT token");
        }
    }

    public String resolveName(HttpServletRequest request) {
        var token = resolveToken(request);
        if (token == null) {
            return null;
        }

        return resolveClaims(token).getSubject();
    }

    Authentication createAuthentication(@NotNull String token) {
        var claims = resolveClaims(token);

        var username = claims.getSubject();

        //noinspection unchecked
        var authorities = (List<String>) claims.get(CLAIM_NAME, List.class);
        var grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
    }
}
