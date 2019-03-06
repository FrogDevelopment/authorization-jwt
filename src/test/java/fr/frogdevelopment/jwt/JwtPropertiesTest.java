package fr.frogdevelopment.jwt;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import org.junit.jupiter.api.Test;

class JwtPropertiesTest {

    @Test
    void shouldThrowExceptionWhenMissingSecretKeyParameter() {
        JwtProperties properties = new JwtProperties();

        assertThrows(InvalidParameterException.class, properties::init, "security.jwt.token.signing-key required !!");
    }

}
