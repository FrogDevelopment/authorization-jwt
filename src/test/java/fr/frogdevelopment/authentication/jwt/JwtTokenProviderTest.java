package fr.frogdevelopment.authentication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.stream.Collectors;

import static fr.frogdevelopment.authentication.jwt.JwtTokenProvider.CLAIM_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(TestConfig.class)
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void createToken() {
        // given
        var username = "USERNAME";
        var grantedAuthorities = List.of("ADMIN", "USER").stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        var authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);

        // when
        var token = jwtTokenProvider.createToken(authentication);

        // then
        assertNotNull(token);
    }

    @Test
    void resolveToken_should_return_null_when_no_header() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        String token = jwtTokenProvider.resolveToken(request);

        // then
        assertNull(token);
    }

    @Test
    void resolveToken_should_return_null_when_bad_header() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(jwtProperties.getHeader(), "BAD TOKEN");

        // when
        String token = jwtTokenProvider.resolveToken(request);

        // then
        assertNull(token);
    }

    @Test
    void resolveToken_should_return_the_token_without_prefix() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(jwtProperties.getHeader(), jwtProperties.getPrefix() + "TOKEN TO RETURN");

        // when
        String token = jwtTokenProvider.resolveToken(request);

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
        Claims claims = jwtTokenProvider.resolveClaims(token);

        // then
        assertEquals(claims.getSubject(), username);
    }

    @Test
    void resolveClaims_should_throw_an_exception() {
        // given
        String token = "BAD TOKEN";


        // when
        assertThrows(BadCredentialsException.class, () -> jwtTokenProvider.resolveClaims(token));
    }

    @Test
    void resolveName() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(jwtProperties.getHeader(), "BAD TOKEN");


        // when
        String resolvedName = jwtTokenProvider.resolveName(request);

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
        request.addHeader(jwtProperties.getHeader(), jwtProperties.getPrefix() + token);


        // when
        String resolvedName = jwtTokenProvider.resolveName(request);

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
                .claim(CLAIM_NAME, roles)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(jwtProperties.getHeader(), jwtProperties.getPrefix() + token);


        // when
        var authentication = jwtTokenProvider.createAuthentication(token);

        // then
        assertNotNull(authentication);
        assertEquals(authentication.getPrincipal(), username);
        assertNull(authentication.getCredentials());
        var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        assertEquals(authentication.getAuthorities(), authorities);
    }

}
