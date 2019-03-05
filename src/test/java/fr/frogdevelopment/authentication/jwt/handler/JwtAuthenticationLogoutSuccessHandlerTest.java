package fr.frogdevelopment.authentication.jwt.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

import fr.frogdevelopment.authentication.jwt.JwtParser;
import fr.frogdevelopment.authentication.jwt.JwtUserDetailsService;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class JwtAuthenticationLogoutSuccessHandlerTest extends AbstractJwtAuthenticationSuccessHandlerTest {

    @InjectMocks
    private JwtAuthenticationLogoutSuccessHandler jwtAuthenticationLogoutSuccessHandler;

    @Mock
    private JwtParser jwtParser;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;

    @Test
    void shouldAddRevokedToken() throws IOException, ServletException {
        // given
        given(jwtParser.getIdFromRefreshToken(any())).willReturn("JTI");

        // when
        jwtAuthenticationLogoutSuccessHandler.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);

        // then
        verify(jwtUserDetailsService).addRevokedToken("JTI");
    }

    @Test
    void shouldNotAddRevokedToken() throws IOException, ServletException {
        // given
        given(jwtParser.getIdFromRefreshToken(any())).willReturn(null);

        // when
        jwtAuthenticationLogoutSuccessHandler.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);

        // then
        verifyNoMoreInteractions(jwtUserDetailsService);
    }
}
