package fr.frogdevelopment.authentication.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.JwtException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Spring boot 2 mockito2 Junit5 example")
class JwtTokenFilterTest {

    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

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
        when(jwtTokenProvider.createAuthentication(anyString())).thenThrow(JwtException.class);

        // when
        whenCalled();

        // then
        assertThat(getAuthenticationFromContext()).isNull();
    }

    @Test
    void shouldSetToken() {
        // given
        givenToken("TOKEN");
        when(jwtTokenProvider.createAuthentication("TOKEN")).thenReturn(mock(Authentication.class));

        // when
        whenCalled();

        // then
        assertThat(getAuthenticationFromContext()).isNotNull();
    }

    private void givenToken(String token) {
        when(jwtTokenProvider.resolveToken(httpServletRequest)).thenReturn(token);
    }

    private void whenCalled() {
        jwtTokenFilter.setTokenOnSpringSecurityContext(httpServletRequest);
    }

    private Authentication getAuthenticationFromContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}