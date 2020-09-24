package fr.frogdevelopment.jwt;

import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResolveTokenToAuthentication {

    private final RetrieveTokenFromRequest retrieveTokenFromRequest;
    private final TokenToAuthentication tokenToAuthentication;

    public ResolveTokenToAuthentication(RetrieveTokenFromRequest retrieveTokenFromRequest,
                                 TokenToAuthentication tokenToAuthentication) {
        this.retrieveTokenFromRequest = retrieveTokenFromRequest;
        this.tokenToAuthentication = tokenToAuthentication;
    }

    @Nullable
    JwtAuthenticationToken call(@NotNull HttpServletRequest request) {
        var token = retrieveTokenFromRequest.call(request);
        if (token == null) {
            return null;
        }

        return tokenToAuthentication.call(token);
    }

}
