package fr.frogdevelopment.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.jetbrains.annotations.NotNull;

public class ResolveClaimsFromToken {

    private final JwtProperties jwtProperties;

    public ResolveClaimsFromToken(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * @see JwtParser#parseClaimsJws(String)
     */
    @NotNull
    public Claims call(@NotNull String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSigningKey().getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

}
