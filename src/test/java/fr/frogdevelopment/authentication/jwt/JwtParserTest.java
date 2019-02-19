package fr.frogdevelopment.authentication.jwt;

import static fr.frogdevelopment.authentication.jwt.JwtParser.TOKEN_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import fr.frogdevelopment.authentication.jwt.conf.JwtApplication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ActiveProfiles("test")
@SpringJUnitConfig(JwtApplication.class)
@SpringBootTest
class JwtParserTest {

    @Autowired
    private JwtParser jwtParser;
    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void resolveToken_should_return_null_when_no_header() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        String token = jwtParser.retrieveToken(request);

        // then
        assertNull(token);
    }

    @Test
    void resolveToken_should_return_null_when_bad_header() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "BAD TOKEN");

        // when
        String token = jwtParser.retrieveToken(request);

        // then
        assertNull(token);
    }

    @Test
    void resolveToken_should_return_the_token_without_prefix() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, TOKEN_TYPE + "TOKEN TO RETURN");

        // when
        String token = jwtParser.retrieveToken(request);

        // then
        assertEquals("TOKEN TO RETURN", token);
    }

    @Test
    void resolveClaims() {
        // given
        var username = "USERNAME";
        String token = Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();

        // when
        Claims claims = jwtParser.resolveClaims(token);

        // then
        assertEquals(claims.getSubject(), username);
    }

    @Test
    void resolveClaims_should_throw_an_exception() {
        // given
        String token = "BAD TOKEN";

        // when
        assertThrows(BadCredentialsException.class, () -> jwtParser.resolveClaims(token));
    }

    @Test
    void resolveName() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "BAD TOKEN");

        // when
        String resolvedName = jwtParser.resolveName(request);

        // then
        assertNull(resolvedName);
    }

    @Test
    void resolveName_should_return_null() {
        // given
        var username = "USERNAME";
        String token = Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, TOKEN_TYPE + token);

        // when
        String resolvedName = jwtParser.resolveName(request);

        // then
        assertEquals(username, resolvedName);
    }

    @Test
    void createAuthentication() {
        // given
        var username = "USERNAME";
        var roles = List.of("ADMIN", "USER");
        String token = Jwts.builder()
                .setSubject(username)
                .claim(jwtProperties.getAuthoritiesKey(), roles)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, TOKEN_TYPE + token);

        // when
        var authentication = jwtParser.createAuthentication(token);

        // then
        assertNotNull(authentication);
        assertEquals(authentication.getPrincipal(), username);
        assertNull(authentication.getCredentials());
        var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        assertEquals(authentication.getAuthorities(), authorities);
    }

}
