package fr.frogdevelopment.authentication.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Filter to validate user credentials and add token in the response header
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    public JwtLoginFilter(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler(jwtTokenProvider));
    }
}
