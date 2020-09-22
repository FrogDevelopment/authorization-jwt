package fr.frogdevelopment.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.jetbrains.annotations.NotNull;

public class ResolveClaimsFromToken {

    private final String signingKey;

    ResolveClaimsFromToken(String signingKey) {
        this.signingKey = signingKey;
    }

    /**
     * @see JwtParser#parseClaimsJws(String)
     */
    @NotNull
    public Claims call(@NotNull String token) {
        return Jwts.parser()
                .setAllowedClockSkewSeconds(5)
                .setSigningKey(signingKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

}
