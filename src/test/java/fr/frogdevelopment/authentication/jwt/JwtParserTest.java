package fr.frogdevelopment.authentication.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RunWith(JUnitPlatform.class)
@ExtendWith(MockitoExtension.class)
class JwtParserTest {

    private static final String SECRET_KEY = "SECRET_KEY";
    private static final String USERNAME = "USERNAME";

    @InjectMocks
    private JwtParser jwtParser;
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private RefreshTokenVerifier refreshTokenVerifier;
    @Mock
    private RequestParser requestParser;

    @Test
    void resolveName_shouldReturn_null_when_incorrect_request() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(requestParser.retrieveToken(request)).willReturn(null);

        // when
        String resolveName = jwtParser.resolveName(request);

        // then
        assertNull(resolveName);
    }

    @Test
    void resolveName_should_throw_exception_when_wrong_signed_token() {
        // given
        givenSecretKey();

        String token = Jwts.builder()
                .setSubject(USERNAME)
                .signWith(SignatureAlgorithm.HS512, "OTHER_SECRET_KEY")
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        given(requestParser.retrieveToken(request)).willReturn(token);

        // when
        assertThrows(SignatureException.class, () -> jwtParser.resolveName(request));
    }

    @Test
    void resolveName_should_throw_exception_when_expired_token() {
        // given
        givenSecretKey();

        String token = Jwts.builder()
                .setSubject(USERNAME)
                .setExpiration(DateUtils.toDate(LocalDateTime.now().minusMinutes(1)))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        given(requestParser.retrieveToken(request)).willReturn(token);

        // when
        assertThrows(ExpiredJwtException.class, () -> jwtParser.resolveName(request));
    }

    @Test
    void resolveName_should_return_name_from_token() {
        // given
        givenSecretKey();

        String token = Jwts.builder()
                .setSubject(USERNAME)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        given(requestParser.retrieveToken(request)).willReturn(token);

        // when
        String resolvedName = jwtParser.resolveName(request);

        // then
        assertEquals(USERNAME, resolvedName);
    }


    @Test
    void refreshToken_shouldReturn_null_when_incorrect_request() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(requestParser.retrieveToken(request)).willReturn(null);

        // when
        String resolveName = jwtParser.refreshToken(request);

        // then
        assertNull(resolveName);
    }

    @Test
    void refreshToken_should_return_throw_an_exception_when_revoked() {
        // given
        givenSecretKey();

        String token = Jwts.builder()
                .setSubject(USERNAME)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        willThrow(RevokedTokenException.class)
                .given(refreshTokenVerifier).verify(any());

        MockHttpServletRequest request = new MockHttpServletRequest();
        given(requestParser.retrieveToken(request)).willReturn(token);

        // when
        assertThrows(RevokedTokenException.class, () -> jwtParser.refreshToken(request));
    }

    @Test
    void refreshToken_should_return_username() {
        // given
        givenSecretKey();

        String token = Jwts.builder()
                .setSubject(USERNAME)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        given(requestParser.retrieveToken(request)).willReturn(token);

        // when
        String resolvedName = jwtParser.refreshToken(request);

        // then
        assertEquals(USERNAME, resolvedName);
    }

    @Test
    void createAuthentication_should_return_authentication() {
        // given
        givenSecretKey();

        var roles = List.of("ADMIN", "USER");
        String token = Jwts.builder()
                .setSubject(USERNAME)
                .claim(TokenProvider.AUTHORITIES_KEY, roles)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        given(requestParser.retrieveToken(request)).willReturn(token);

        // when
        var authentication = jwtParser.createAuthentication(request);

        // then
        assertNotNull(authentication);
        assertEquals(USERNAME, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        assertEquals(authentication.getAuthorities(), authorities);
    }

    private void givenSecretKey() {
        given(jwtProperties.getSecretKey()).willReturn(SECRET_KEY);
    }

}
