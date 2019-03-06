package fr.frogdevelopment.jwt;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RetrieveTokenFromRequest {

    public static final String TOKEN_TYPE = "Bearer ";

    @Nullable
    public String call(@NotNull HttpServletRequest request) {
        var bearer = request.getHeader(AUTHORIZATION);
        if (bearer == null || !bearer.startsWith(TOKEN_TYPE)) {
            return null;
        }

        return bearer.replace(TOKEN_TYPE, "");
    }

}
