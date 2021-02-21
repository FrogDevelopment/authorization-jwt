package com.frogdevelopment.jwt;

import io.jsonwebtoken.JwtException;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtProcessTokenFilter extends OncePerRequestFilter {

    private final List<String> excludePatterns;
    private final ResolveTokenToAuthentication resolveTokenToAuthentication;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public JwtProcessTokenFilter(final ResolveTokenToAuthentication resolveTokenToAuthentication) {
        this(null, resolveTokenToAuthentication);
    }

    public JwtProcessTokenFilter(final List<String> excludePatterns,
                                 final ResolveTokenToAuthentication resolveTokenToAuthentication) {
        this.excludePatterns = excludePatterns;
        this.resolveTokenToAuthentication = resolveTokenToAuthentication;
    }

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {
        log.debug("Processing [{}]", request.getRequestURI());
        if (requiresAuthentication(request)) {
            resolveTokenAndSetAuthenticationOnSpringSecurityContext(request);
        }

        // go to the next filter in the filter chain
        filterChain.doFilter(request, response);
    }

    protected boolean requiresAuthentication(final HttpServletRequest request) {
        final var requestURI = request.getRequestURI();
        return this.excludePatterns == null
               || this.excludePatterns
                       .stream()
                       .noneMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    private void resolveTokenAndSetAuthenticationOnSpringSecurityContext(@NonNull final HttpServletRequest request) {
        try {
            log.debug("Resolve token and set authentication on Spring Security Context for request [{}]",
                    request.getRequestURI());
            final var authentication = resolveTokenToAuthentication.call(request);

            if (authentication != null) {
                log.debug("Setting resolved token authentication to Spring Security Context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (final JwtException ex) {
            log.error("Unable to resolve token for request [{}], got message: {}", request.getRequestURL().toString(), ex.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
}
