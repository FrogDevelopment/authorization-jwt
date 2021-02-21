package com.frogdevelopment.jwt;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class ResolveTokenToAuthentication {

    private final RetrieveTokenFromRequest retrieveTokenFromRequest;
    private final TokenToAuthentication tokenToAuthentication;

    public ResolveTokenToAuthentication(final RetrieveTokenFromRequest retrieveTokenFromRequest,
                                        final TokenToAuthentication tokenToAuthentication) {
        this.retrieveTokenFromRequest = retrieveTokenFromRequest;
        this.tokenToAuthentication = tokenToAuthentication;
    }

    @Nullable
    JwtAuthenticationToken call(@NotNull final HttpServletRequest request) {
        final var token = retrieveTokenFromRequest.call(request);
        if (token == null) {
            log.debug("No token found for request [{}]", request.getRequestURI());
            return null;
        }

        return tokenToAuthentication.call(token);
    }

}
