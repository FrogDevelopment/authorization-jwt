package com.frogdevelopment.jwt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtSecurityContextHelper {

    public static JwtAuthenticationToken getJwtAuthenticationToken() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Not AuthenticationToken in the SecurityContext");
        }

        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new InsufficientAuthenticationException("Not a JwtAuthenticationToken");
        }

        return (JwtAuthenticationToken) authentication;
    }

}
