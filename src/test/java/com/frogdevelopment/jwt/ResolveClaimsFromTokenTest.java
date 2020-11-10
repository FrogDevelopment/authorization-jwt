package com.frogdevelopment.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@Tag("unitTest")
@RunWith(JUnitPlatform.class)
class ResolveClaimsFromTokenTest {

    private static final String USERNAME = "USERNAME";
    private static final String SIGNING_KEY = "my-signing-key";
    private static final String OTHER_SIGNING_KEY = "other-signing-key";

    @Test
    void shouldReturnClaimsWhenCorrectSigningKeys() {
        // given
        var resolveClaimsFromToken = new ResolveClaimsFromToken(SIGNING_KEY);
        var token = givenToken(SIGNING_KEY);

        // when
        var claims = resolveClaimsFromToken.call(token);

        // then
        assertEquals(USERNAME, claims.getSubject());
    }

    @Test
    void shouldThrowAnExceptionWhenIncorrectSigningKey() {
        // given
        var resolveClaimsFromToken = new ResolveClaimsFromToken(OTHER_SIGNING_KEY);
        var token = givenToken(SIGNING_KEY);

        // when
        assertThrows(SignatureException.class, () -> resolveClaimsFromToken.call(token));
    }

    @Test
    void shouldThrowAnExceptionWhenIncorrectSigningKeyOnToken() {
        // given
        var resolveClaimsFromToken = new ResolveClaimsFromToken(SIGNING_KEY);
        var token = givenToken(OTHER_SIGNING_KEY);

        // when
        assertThrows(SignatureException.class, () -> resolveClaimsFromToken.call(token));
    }

    private String givenToken(String signingKey) {
        return Jwts.builder()
                .setSubject(USERNAME)
                .signWith(SignatureAlgorithm.HS512, signingKey.getBytes())
                .compact();
    }
}
