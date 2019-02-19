package fr.frogdevelopment.authentication.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.util.CollectionUtils;

@Builder
public class Token {

    private String issuer;
    private String subject;
    private String authoritiesKey;
    private Set<String> authorities;
    private long expiration;
    private String secretKey;

    @Getter
    private Date expirationDate;

    public String toJwt() {
        if (CollectionUtils.isEmpty(authorities)) {
            throw new InsufficientAuthenticationException("User has no authorities assigned");
        }

        LocalDateTime issuedAt = LocalDateTime.now();
        expirationDate = toDate(issuedAt.plusMinutes(expiration));

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(toDate(issuedAt))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .claim(authoritiesKey, authorities)
                .compact();
    }

    @NotNull
    private Date toDate(LocalDateTime expiration) {
        return Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
    }

}
