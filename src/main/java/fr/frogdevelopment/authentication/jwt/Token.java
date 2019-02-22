package fr.frogdevelopment.authentication.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
public class Token {

    private String id;
    private String issuer;
    @NonNull
    private String subject;
    private Map<String, Object> claims;
    @NonNull
    private long expiration;
    @NonNull
    private ChronoUnit chronoUnit;
    @NonNull
    private String secretKey;

    private Date expirationDate;

    public String toJwt() {
        LocalDateTime issuedAt = LocalDateTime.now();
        expirationDate = toDate(issuedAt.plus(expiration, chronoUnit));

        return Jwts.builder()
                .setId(id)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(toDate(issuedAt))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .addClaims(claims)
                .compact();
    }

    @NotNull
    private Date toDate(LocalDateTime expiration) {
        return Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
    }

}
