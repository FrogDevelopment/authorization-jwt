package fr.frogdevelopment.authentication.jwt;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
class RequestParser {

    static final String TOKEN_TYPE = "Bearer ";

    String retrieveToken(@NotNull HttpServletRequest request) {
        var bearer = request.getHeader(AUTHORIZATION);
        if (bearer == null || !bearer.startsWith(TOKEN_TYPE)) {
            return null;
        }

        return bearer.replace(TOKEN_TYPE, "");
    }

}
