package fr.frogdevelopment.authentication.jwt;

import static java.lang.String.format;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
public class TokenProvider {

    static final String AUTHORITIES_KEY = "authorities";

    private final String issuer; // fixme should be user-service url
    private final JwtProperties jwtProperties;

    @Autowired
    public TokenProvider(@Value("${spring.application.name}") String issuer,
                         JwtProperties jwtProperties) {
        this.issuer = issuer;
        this.jwtProperties = jwtProperties;
    }

    @NotNull
    public Token createAccessToken(@NotNull UserDetails userDetails) {
        return createAccessToken(
                userDetails.getUsername(),
                userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
    }

    @NotNull
    public Token createAccessToken(@NotNull Authentication authentication) {
        return createAccessToken(
                authentication.getName(),
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
    }

    @NotNull
    private Token createAccessToken(String subject, Set<String> authorities) {
        if (CollectionUtils.isEmpty(authorities)) {
            throw new InsufficientAuthenticationException(format("User %s has no authorities assigned", subject));
        }

        return Token.builder()
                .issuer(issuer)
                .subject(subject)
                .claims(Map.of(AUTHORITIES_KEY, authorities))
                .expiration(jwtProperties.getAccessTokenExpiration())
                .chronoUnit(ChronoUnit.SECONDS)
                .secretKey(jwtProperties.getSecretKey())
                .build();
    }

    @NotNull
    public Token createRefreshToken(@NotNull Authentication authentication) {
        return Token.builder()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(authentication.getName())
                .expiration(jwtProperties.getRefreshTokenExpiration())
                .chronoUnit(ChronoUnit.DAYS)
                .secretKey(jwtProperties.getSecretKey())
                .build();
    }

}
