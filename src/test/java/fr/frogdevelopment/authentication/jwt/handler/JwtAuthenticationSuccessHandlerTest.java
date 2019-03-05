package fr.frogdevelopment.authentication.jwt.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.frogdevelopment.authentication.jwt.Token;
import fr.frogdevelopment.authentication.jwt.TokenProvider;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationSuccessHandlerTest extends AbstractJwtAuthenticationSuccessHandlerTest {

    @InjectMocks
    private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Map<String, Object>> bodyTokenCaptor;

    @Test
    void shouldWriteTheTokenToTheBodyAndReturnOK() throws IOException {
        // given
        givenAccessToken();
        givenRefreshToken();

        // when
        jwtAuthenticationSuccessHandler
                .onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

        // then
        verify(objectMapper).writeValue(eq(writer), bodyTokenCaptor.capture());
        assertThat(bodyTokenCaptor.getValue()).containsKeys("accessToken", "expirationDate", "refreshToken");

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        verify(httpServletResponse).flushBuffer();
        verify(writer).close();
    }

    @Test
    void shouldReturnErrorWhenException() throws IOException {
        // given
        givenAccessToken();
        givenRefreshToken();
        doThrow(IOException.class).when(objectMapper).writeValue(eq(writer), any());

        // when
        jwtAuthenticationSuccessHandler
                .onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

        // then
        verify(httpServletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writer).close();
    }

    private Token givenToken() {
        return Token.builder()
                .subject("test")
                .expiration(10)
                .chronoUnit(ChronoUnit.DAYS)
                .secretKey("secret-key")
                .build();
    }

    private void givenAccessToken() {
        given(tokenProvider.createAccessToken(authentication)).willReturn(givenToken());
    }

    private void givenRefreshToken() {
        given(tokenProvider.createRefreshToken(authentication)).willReturn(givenToken());
    }
}
