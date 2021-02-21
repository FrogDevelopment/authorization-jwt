package com.frogdevelopment.jwt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TokenToAuthentication {

    private final ResolveClaimsFromToken resolveClaimsFromToken;

    public TokenToAuthentication(final ResolveClaimsFromToken resolveClaimsFromToken) {
        this.resolveClaimsFromToken = resolveClaimsFromToken;
    }

    @Nullable
    public JwtAuthenticationToken call(@NotNull final String token) {
        final var claims = resolveClaimsFromToken.call(token);

        return new JwtAuthenticationToken(claims, token);
    }

}
