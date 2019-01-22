package fr.frogdevelopment.authentication.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationSuccessHandlerTest {

    @InjectMocks
    private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private Authentication authentication;
    @Mock
    private PrintWriter writer;

    @Captor
    private ArgumentCaptor<String> bodyTokenCaptor;

    @Test
    void shouldWriteTheTokenToTheBodyAndReturnOK() throws IOException {
        // given
        when(jwtTokenProvider.createToken(authentication)).thenReturn("MY_TOKEN");
        when(httpServletResponse.getWriter()).thenReturn(writer);

        // when
        jwtAuthenticationSuccessHandler
                .onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

        // then
        verify(writer).write(bodyTokenCaptor.capture());
        assertThat(bodyTokenCaptor.getValue()).isEqualTo("{\"token\":\"MY_TOKEN\"}");

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        verify(httpServletResponse).flushBuffer();
        verify(writer).close();
    }

    @Test
    void shouldReturnErrorWhenException() throws IOException {
        // given
        when(jwtTokenProvider.createToken(authentication)).thenReturn("MY_TOKEN");
        when(httpServletResponse.getWriter()).thenReturn(writer);
        doThrow(IOException.class).when(httpServletResponse).flushBuffer();

        // when
        jwtAuthenticationSuccessHandler
                .onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

        // then
        verify(httpServletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writer).close();
    }

}