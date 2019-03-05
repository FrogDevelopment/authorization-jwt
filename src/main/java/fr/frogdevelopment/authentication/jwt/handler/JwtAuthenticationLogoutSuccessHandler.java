package fr.frogdevelopment.authentication.jwt.handler;

import fr.frogdevelopment.authentication.jwt.JwtParser;
import fr.frogdevelopment.authentication.jwt.JwtUserDetailsService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean({JwtUserDetailsService.class})
public class JwtAuthenticationLogoutSuccessHandler extends HttpStatusReturningLogoutSuccessHandler {

    private final JwtParser jwtParser;
    private final JwtUserDetailsService jwtUserDetailsService;

    public JwtAuthenticationLogoutSuccessHandler(JwtParser jwtParser,
                                                 JwtUserDetailsService jwtUserDetailsService) {
        this.jwtParser = jwtParser;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String jti = jwtParser.getIdFromRefreshToken(request);
        if (jti != null) {
            jwtUserDetailsService.addRevokedToken(jti);
        }

        super.onLogoutSuccess(request, response, authentication);
    }
}
