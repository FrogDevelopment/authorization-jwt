package fr.frogdevelopment.authentication.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Filter to validate user credentials and add token in the response header
 */
@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    public JwtLoginFilter(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            var token = jwtTokenProvider.createToken(authentication);

            // send token as response
            log.info("Authentication success => write token on body");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(token);
                writer.flush();
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (IOException e) {
                log.error("Error while writing authentication token to response", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        });
    }
}
