package fr.frogdevelopment.authentication.jwt.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.frogdevelopment.authentication.jwt.JwtParser;
import io.jsonwebtoken.JwtException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtProcessTokenFilterTest {

    @InjectMocks
    private JwtProcessTokenFilter jwtProcessTokenFilter;

    @Mock
    private JwtParser jwtParser;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    void shouldClearSecurityContextWhenTokenIsNull() {
        // given
        givenToken(null);

        // when
        whenCalled();

        // then
        assertThat(getAuthenticationFromContext()).isNull();
    }

    @Test
    void shouldClearSecurityContextWhenJwtExceptionIsRaised() {
        // given
        givenToken("TOKEN");
//        when(jwtParser.createAuthentication(anyString())).thenThrow(JwtException.class);

        // when
        whenCalled();

        // then
        assertThat(getAuthenticationFromContext()).isNull();
    }

    @Test
    void shouldSetToken() {
        // given
        givenToken("TOKEN");
        when(jwtParser.createAuthentication(httpServletRequest)).thenReturn(mock(Authentication.class));

        // when
        whenCalled();

        // then
        assertThat(getAuthenticationFromContext()).isNotNull();
    }

    private void givenToken(String token) {
//        when(jwtParser.retrieveToken(httpServletRequest)).thenReturn(token);
    }

    private void whenCalled() {
        jwtProcessTokenFilter.setTokenOnSpringSecurityContext(httpServletRequest);
    }

    private Authentication getAuthenticationFromContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
