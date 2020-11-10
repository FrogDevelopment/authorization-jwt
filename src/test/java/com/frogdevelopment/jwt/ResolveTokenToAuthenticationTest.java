package com.frogdevelopment.jwt;

import static com.frogdevelopment.jwt.JwtAuthenticationToken.AUTHORITIES_KEY;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
class ResolveTokenToAuthenticationTest {

    private static final String USERNAME = "USERNAME";
    private static final byte[] SIGNING_KEY = "my-signing-key".getBytes();
    private static final List<String> ROLES = List.of("ADMIN", "USER");

    @InjectMocks
    private ResolveTokenToAuthentication resolveTokenToAuthentication;
    @Mock
    private RetrieveTokenFromRequest retrieveTokenFromRequest;
    @Mock
    private TokenToAuthentication tokenToAuthentication;

    @Test
    void should_return_null_when_no_token() {
        // given
        var request = new MockHttpServletRequest();
        givenNotToken(request);

        // when
        var authentication = resolveTokenToAuthentication.call(request);

        // then
        assertThat(authentication).isNull();
        then(tokenToAuthentication).shouldHaveNoInteractions();
    }

    @Test
    void should_return_authentication_from_token() {
        // given
        var request = new MockHttpServletRequest();
        givenToken(request);
        givenJwtAuthenticationToken();

        // when
        var authentication = resolveTokenToAuthentication.call(request);

        // then
        assertThat(authentication).isNotNull();
    }

    private void givenToken(MockHttpServletRequest request) {
        given(retrieveTokenFromRequest.call(request)).willReturn(Jwts.builder()
                .setSubject(USERNAME)
                .addClaims(Map.of(AUTHORITIES_KEY, ROLES))
                .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
                .compact());
    }

    private void givenNotToken(MockHttpServletRequest request) {
        given(retrieveTokenFromRequest.call(request)).willReturn(null);
    }

    private void givenJwtAuthenticationToken() {
        given(tokenToAuthentication.call(anyString()))
                .willAnswer(i -> new JwtAuthenticationToken(new DefaultClaims(), "token"));
    }

}
