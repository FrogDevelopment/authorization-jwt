package fr.frogdevelopment.authentication.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;

@Data
@Component
@ConfigurationProperties("security.jwt.token")
public class JwtProperties {

    private String secretKey;
    private String header = "Authorization";
    private String prefix = "Bearer ";
    private long expiration = 60000;

    @PostConstruct
    void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
