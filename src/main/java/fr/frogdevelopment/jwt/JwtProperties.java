package fr.frogdevelopment.jwt;

import java.security.InvalidParameterException;
import javax.annotation.PostConstruct;
import lombok.Data;

@Data
public class JwtProperties {

    public static final String AUTHORITIES_KEY = "authorities";

    private String signingKey;

    @PostConstruct
    void init() {
        if (signingKey == null) {
            throw new InvalidParameterException("security.jwt.token.signing-key required !!");
        }
    }
}
