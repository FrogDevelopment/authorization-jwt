package fr.frogdevelopment.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResolveClaimsFromTokenTest {

    private static final String USERNAME = "USERNAME";
    private static final String SIGNING_KEY = "my-signing-key";
    private static final String OTHER_SIGNING_KEY = "other-signing-key";

    @InjectMocks
    private ResolveClaimsFromToken resolveClaimsFromToken;

    @Mock
    private JwtProperties jwtProperties;

    @Test
    void shouldReturnClaimsWhenCorrectSigningKeys() {
        // given
        String token = givenToken(SIGNING_KEY);
        givenSigningKey(SIGNING_KEY);

        // when
        var claims = resolveClaimsFromToken.call(token);

        // then
        assertEquals(USERNAME, claims.getSubject());
    }

    @Test
    void shouldThrowAnExceptionWhenIncorrectSigningKey() {
        // given
        String token = givenToken(SIGNING_KEY);
        givenSigningKey(OTHER_SIGNING_KEY);

        // when
        assertThrows(SignatureException.class, () -> resolveClaimsFromToken.call(token));
    }

    @Test
    void shouldThrowAnExceptionWhenIncorrectSigningKeyOnToken() {
        // given
        String token = givenToken(OTHER_SIGNING_KEY);
        givenSigningKey(SIGNING_KEY);

        // when
        assertThrows(SignatureException.class, () -> resolveClaimsFromToken.call(token));
    }

    private String givenToken(String signingKey) {
        return Jwts.builder()
                .setSubject(USERNAME)
                .signWith(SignatureAlgorithm.HS512, signingKey.getBytes())
                .compact();
    }

    private void givenSigningKey(String signingKey) {
        given(jwtProperties.getSigningKey()).willReturn(signingKey);
    }


}
