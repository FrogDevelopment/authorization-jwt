package fr.frogdevelopment.jwt;

import io.jsonwebtoken.JwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtProcessTokenFilter extends OncePerRequestFilter {

    private final ResolveTokenToAuthentication resolveTokenToAuthentication;

    JwtProcessTokenFilter(ResolveTokenToAuthentication resolveTokenToAuthentication) {
        this.resolveTokenToAuthentication = resolveTokenToAuthentication;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        resolveTokenAndSetAuthenticationOnSpringSecurityContext(request);

        // go to the next filter in the filter chain
        filterChain.doFilter(request, response);
    }

    private void resolveTokenAndSetAuthenticationOnSpringSecurityContext(@NonNull HttpServletRequest request) {
        try {
            log.debug("Resolve token and set authentication on Spring Security Context for request {}",
                    request.getRequestURL());
            Authentication authentication = resolveTokenToAuthentication.call(request);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException ex) {
            log.error("Error while trying to resolve token", ex);
            SecurityContextHolder.clearContext();
        }
    }
}
