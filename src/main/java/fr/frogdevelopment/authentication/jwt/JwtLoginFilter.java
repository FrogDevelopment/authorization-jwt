package fr.frogdevelopment.authentication.jwt;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Filter to validate user credentials and add token in the response header
 */
@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    public JwtLoginFilter(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(getAuthenticationSuccessHandler(jwtTokenProvider));
    }

    @NotNull
    private AuthenticationSuccessHandler getAuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        return (request, response, authentication) -> {
            var token = jwtTokenProvider.createToken(authentication);

            // send token as response
            log.info("Authentication success => write token on body");
            try (PrintWriter writer = response.getWriter()) {
                writer.write("{\"token\":\"" + token + "\"}");
                response.setStatus(HttpServletResponse.SC_OK);
                response.flushBuffer();
            } catch (IOException e) {
                log.error("Error while writing authentication token to response", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        };
    }
}
