package fr.frogdevelopment.authentication.jwt;

import java.security.InvalidParameterException;
import java.util.Base64;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@ConfigurationProperties("security.jwt.token")
public class JwtProperties {

    private String secretKey;
    private long accessTokenExpirationTime = 600000;
    private long refreshTokenExpirationTime = 600000;

    @PostConstruct
    void init() {
        if (secretKey == null) {
            throw new InvalidParameterException("security.jwt.token.secret-key required !!");
        }
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
