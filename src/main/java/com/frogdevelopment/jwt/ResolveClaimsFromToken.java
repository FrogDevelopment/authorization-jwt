package com.frogdevelopment.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.jetbrains.annotations.NotNull;

/**
 * @see JwtParser#parseClaimsJws(String)
 */
public class ResolveClaimsFromToken {

    private final String signingKey;

    ResolveClaimsFromToken(final String signingKey) {
        this.signingKey = signingKey;
    }

    @NotNull
    public Claims call(@NotNull final String token) {
        return Jwts.parser()
                .setAllowedClockSkewSeconds(5)
                .setSigningKey(signingKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

}
