package fr.frogdevelopment.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.jsonwebtoken.JwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
class JwtProcessTokenFilterTest {

    public static class TestJwtProcessTokenFilter extends JwtProcessTokenFilter {

        public TestJwtProcessTokenFilter(ResolveTokenToAuthentication resolveTokenToAuthentication) {
            super(resolveTokenToAuthentication);
        }

        void call(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            doFilterInternal(request, response, filterChain);
        }
    }

    @InjectMocks
    private TestJwtProcessTokenFilter jwtProcessTokenFilter;

    @Mock
    private ResolveTokenToAuthentication resolveTokenToAuthentication;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Test
    void shouldClearSecurityContextWhenAuthenticationIsNull() throws IOException, ServletException {
        // given
        givenAuthentication(null);

        // when
        whenCalled();

        // then
        thenAuthenticationFromContextIsNull();
        thenDoNextFilterChain();
    }

    @Test
    void shouldClearSecurityContextWhenJwtExceptionIsRaised() throws ServletException, IOException {
        // given
        givenAnExceptionWhenResolvingToken();

        // when
        whenCalled();

        // then
        thenAuthenticationFromContextIsNull();
        thenDoNextFilterChain();
    }

    @Test
    void shouldSetToken() throws ServletException, IOException {
        // given
        givenAuthentication(mock(JwtAuthenticationToken.class));

        // when
        whenCalled();

        // then
        thenAuthenticationFromContextIsNotNull();
        thenDoNextFilterChain();
    }

    private void givenAuthentication(JwtAuthenticationToken authentication) {
        given(resolveTokenToAuthentication.call(request)).willReturn(authentication);
    }

    private void givenAnExceptionWhenResolvingToken() {
        given(resolveTokenToAuthentication.call(any())).willThrow(JwtException.class);
    }

    private void whenCalled() throws ServletException, IOException {
        jwtProcessTokenFilter.call(request, response, filterChain);
    }

    private Authentication getAuthenticationFromContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private void thenAuthenticationFromContextIsNull() {
        assertThat(getAuthenticationFromContext()).isNull();
    }

    private void thenAuthenticationFromContextIsNotNull() {
        assertThat(getAuthenticationFromContext()).isNotNull();
    }

    private void thenDoNextFilterChain() throws IOException, ServletException {
        verify(filterChain).doFilter(request, response);
    }
}
