package fr.frogdevelopment.authentication.jwt;

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
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        setTokenOnSpringSecurityContext(request);

        // go to the next filter in the filter chain
        filterChain.doFilter(request, response);
    }

    void setTokenOnSpringSecurityContext(@NonNull HttpServletRequest request) {
        // resolve token
        var token = jwtTokenProvider.resolveToken(request);

        if (token != null) {
            try {
                // parsing token
                Authentication authentication = jwtTokenProvider.createAuthentication(token);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException ex) {
                log.error("Error while trying to resolve token", ex);
                // In case of failure. Make sure it's clear; so guarantee user won't be authenticated
                SecurityContextHolder.clearContext();
            }
        }
    }
}
