package fr.frogdevelopment.authentication.jwt;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
public class JwtTokenProvider {

    static final String TOKEN_TYPE = "Bearer ";
    static final String AUTHORITIES_NAME = "authorities";

    private final JwtProperties jwtProperties;
    private final TokenVerifier tokenVerifier;

    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties,
                            TokenVerifier tokenVerifier) {
        this.jwtProperties = jwtProperties;
        this.tokenVerifier = tokenVerifier;
    }

    public String createAccessToken(@NotNull UserDetails userDetails) {
        if (userDetails.getAuthorities() == null) {
            throw new InsufficientAuthenticationException("User has no roles assigned");
        }

        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return getJwtBuilder(
                userDetails.getUsername(),
                authorities,
                jwtProperties.getAccessTokenExpirationTime())
                .compact();
    }

    public String createAccessToken(Authentication authentication) {
        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return getJwtBuilder(
                authentication.getName(),
                authorities,
                jwtProperties.getAccessTokenExpirationTime())
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {

        List<String> authorities = Collections.singletonList("ROLE_REFRESH_TOKEN");

        JwtBuilder jwtBuilder = getJwtBuilder(
                authentication.getName(),
                authorities,
                jwtProperties.getRefreshTokenExpirationTime());

        return jwtBuilder
                .setId(UUID.randomUUID().toString())
                .compact();
    }

    private JwtBuilder getJwtBuilder(String subject, List<String> authorities,
                                     long tokenExpirationTime) {
        if (CollectionUtils.isEmpty(authorities)) {
            throw new InsufficientAuthenticationException("User has no authorities assigned");
        }

        var now = LocalDateTime.now();
        var expiration = now.plusMinutes(tokenExpirationTime);

        return Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_NAME, authorities)
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(expiration))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey());
    }

    @NotNull
    private Date toDate(LocalDateTime expiration) {
        return Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String resolveToken(@NotNull HttpServletRequest request) {
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

    public Authentication createAuthentication(@NotNull String token) {
        var claims = resolveClaims(token);

        var username = claims.getSubject();

        var grantedAuthorities = resolveAuthorities(claims).stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new JwtAuthenticationToken(username, grantedAuthorities);
    }


    public String refreshToken(@NotNull HttpServletRequest request) {
        var token = resolveToken(request);

        var claims = resolveClaims(token);

        List<String> authorities = resolveAuthorities(claims);
        if (authorities == null
                || authorities.isEmpty()
                || authorities.stream().noneMatch("ROLE_REFRESH_TOKEN"::equals)) {
            throw new JwtException("");
        }

        tokenVerifier.verify(claims.getId());

        return claims.getSubject();

    }

    private List<String> resolveAuthorities(Claims claims) {
        //noinspection unchecked
        return (List<String>) claims.get(AUTHORITIES_NAME, List.class);
    }
}
