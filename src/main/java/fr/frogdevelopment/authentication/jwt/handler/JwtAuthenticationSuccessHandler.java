package fr.frogdevelopment.authentication.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.frogdevelopment.authentication.jwt.JwtTokenProvider;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    JwtAuthenticationSuccessHandler(ObjectMapper objectMapper,
                                    JwtTokenProvider jwtTokenProvider) {
        this.objectMapper = objectMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) {
        var accessToken = jwtTokenProvider.createAccessToken(authentication);
        var refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        // send token as response
        log.info("Authentication success => write token on body");
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            objectMapper.writeValue(writer, tokenMap);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletResponse.flushBuffer();
        } catch (IOException e) {
            log.error("Error while writing authentication token to response", e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        clearAuthenticationAttributes(httpServletRequest);
    }

    /**
     * Removes temporary authentication-related data which may have been stored in the session during the authentication
     * process..
     */
    private final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
