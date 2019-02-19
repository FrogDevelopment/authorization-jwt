package fr.frogdevelopment.authentication.jwt;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

    private final String issuer;
    private final JwtProperties jwtProperties;

    @Autowired
    public TokenProvider(@Value("${spring.application.name}") String issuer,
                         JwtProperties jwtProperties) {
        this.issuer = issuer;
        this.jwtProperties = jwtProperties;
    }

    @NotNull
    public Token createAccessToken(@NotNull UserDetails userDetails) {
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Token.builder()
                .issuer(issuer)
                .subject(userDetails.getUsername())
                .authoritiesKey(jwtProperties.getAuthoritiesKey())
                .authorities(authorities)
                .expiration(jwtProperties.getAccessTokenExpirationTime())
                .secretKey(jwtProperties.getSecretKey())
                .build();
    }

    @NotNull
    public Token createAccessToken(@NotNull Authentication authentication) {
        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Token.builder()
                .issuer(issuer)
                .subject(authentication.getName())
                .authoritiesKey(jwtProperties.getAuthoritiesKey())
                .authorities(authorities)
                .expiration(jwtProperties.getAccessTokenExpirationTime())
                .secretKey(jwtProperties.getSecretKey())
                .build();
    }

    @NotNull
    public Token createRefreshToken(@NotNull Authentication authentication) {
        var authorities = jwtProperties.getRolesRefresh();

        return Token.builder()
                .issuer(issuer)
                .subject(authentication.getName())
                .authoritiesKey(jwtProperties.getAuthoritiesKey())
                .authorities(authorities)
                .expiration(jwtProperties.getRefreshTokenExpirationTime())
                .secretKey(jwtProperties.getSecretKey())
                .build();
    }

}
