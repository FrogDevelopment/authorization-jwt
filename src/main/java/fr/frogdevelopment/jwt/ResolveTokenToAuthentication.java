package fr.frogdevelopment.jwt;

import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class ResolveTokenToAuthentication {

    static final String AUTHORITIES_KEY = "authorities";

    private final ResolveClaimsFromToken resolveClaimsFromToken;
    private final RetrieveTokenFromRequest retrieveTokenFromRequest;

    ResolveTokenToAuthentication(ResolveClaimsFromToken resolveClaimsFromToken,
                                 RetrieveTokenFromRequest retrieveTokenFromRequest) {
        this.resolveClaimsFromToken = resolveClaimsFromToken;
        this.retrieveTokenFromRequest = retrieveTokenFromRequest;
    }

    @Nullable Authentication call(@NotNull HttpServletRequest request) {
        var token = retrieveTokenFromRequest.call(request);
        if (token == null) {
            return null;
        }

        var claims = resolveClaimsFromToken.call(token);

        return new JwtAuthenticationToken(claims.getSubject(), resolveAuthorities(claims));
    }

    private List<SimpleGrantedAuthority> resolveAuthorities(@NotNull Claims claims) {
        //noinspection unchecked
        return ((List<String>) claims.get(AUTHORITIES_KEY, List.class))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
