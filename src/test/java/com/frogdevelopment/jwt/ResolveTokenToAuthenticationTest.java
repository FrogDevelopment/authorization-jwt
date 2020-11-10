package com.frogdevelopment.jwt;

import static com.frogdevelopment.jwt.JwtAuthenticationToken.AUTHORITIES_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
class ResolveTokenToAuthenticationTest {

    private static final String USERNAME = "USERNAME";
    private static final byte[] SIGNING_KEY = "my-signing-key".getBytes();

    @InjectMocks
    private ResolveTokenToAuthentication resolveTokenToAuthentication;
    @Mock
    private ResolveClaimsFromToken resolveClaimsFromToken;
    @Mock
    private RetrieveTokenFromRequest retrieveTokenFromRequest;
    private static final List<String> ROLES = List.of("ADMIN", "USER");

    @Test
    void createAuthentication_should_return_authentication() {
        // given
        MockHttpServletRequest request = givenRequest();
        givenClaims();

        // when
        var authentication = resolveTokenToAuthentication.call(request);

        // then
        assertNotNull(authentication);
        assertEquals(USERNAME, authentication.getPrincipal());
        assertEquals(USERNAME, authentication.getName());
        assertNotNull(authentication.getDetails());
        assertTrue(authentication.isAuthenticated());
        assertNull(authentication.getCredentials());
        var authorities = ROLES.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        assertEquals(authentication.getAuthorities(), authorities);
    }

    private MockHttpServletRequest givenRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(retrieveTokenFromRequest.call(request)).willReturn(givenToken());
        return request;
    }

    private void givenClaims() {
        given(resolveClaimsFromToken.call(anyString())).willAnswer(i -> Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(i.getArgument(0))
                .getBody());
    }

    private String givenToken() {
        return Jwts.builder()
                .setSubject(USERNAME)
                .addClaims(Map.of(AUTHORITIES_KEY, ROLES))
                .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
                .compact();
    }

}
