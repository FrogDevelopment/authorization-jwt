package fr.frogdevelopment.authentication.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RunWith(JUnitPlatform.class)
class TokenTest {


    @Test
    void shouldThrowAnExceptionWhenNonNullFieldsAreNull() {
        assertThrows(NullPointerException.class, () -> Token.builder().build());
    }

    @Test
    void test() {
        Token token = Token.builder()
                .subject("test")
                .expiration(10)
                .chronoUnit(ChronoUnit.CENTURIES)
                .secretKey("SECRET_KEY")
                .build();

        String jwt = token.toJwt();

        assertNotNull(jwt);
    }
}
