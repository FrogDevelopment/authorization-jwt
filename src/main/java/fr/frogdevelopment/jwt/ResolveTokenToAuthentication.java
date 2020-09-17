package fr.frogdevelopment.jwt;

import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ResolveTokenToAuthentication {

    private final ResolveClaimsFromToken resolveClaimsFromToken;
    private final RetrieveTokenFromRequest retrieveTokenFromRequest;

    ResolveTokenToAuthentication(ResolveClaimsFromToken resolveClaimsFromToken,
                                 RetrieveTokenFromRequest retrieveTokenFromRequest) {
        this.resolveClaimsFromToken = resolveClaimsFromToken;
        this.retrieveTokenFromRequest = retrieveTokenFromRequest;
    }

    @Nullable
    JwtAuthenticationToken call(@NotNull HttpServletRequest request) {
        var token = retrieveTokenFromRequest.call(request);
        if (token == null) {
            return null;
        }

        var claims = resolveClaimsFromToken.call(token);

        return new JwtAuthenticationToken(claims);
    }

}
