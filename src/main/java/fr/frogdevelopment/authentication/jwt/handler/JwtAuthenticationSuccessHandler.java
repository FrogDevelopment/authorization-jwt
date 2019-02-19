package fr.frogdevelopment.authentication.jwt.handler;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.frogdevelopment.authentication.jwt.TokenProvider;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;

    JwtAuthenticationSuccessHandler(ObjectMapper objectMapper,
                                    TokenProvider tokenProvider) {
        this.objectMapper = objectMapper;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) {
        var accessToken = tokenProvider.createAccessToken(authentication);
        var refreshToken = tokenProvider.createRefreshToken(authentication);

        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken.toJwt());
        tokenMap.put("expirationDate", accessToken.getExpirationDate());
        tokenMap.put("refreshToken", refreshToken.toJwt());

        // send token as response
        log.info("Authentication success => write token on body");
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            objectMapper.writeValue(writer, tokenMap);
            httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
            httpServletResponse.setStatus(SC_OK);
            httpServletResponse.flushBuffer();
        } catch (IOException e) {
            log.error("Error while writing authentication token to response", e);
            httpServletResponse.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }
}
