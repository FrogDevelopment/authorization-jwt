package fr.frogdevelopment.authentication.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TestConfig {

    @Bean
    public JwtProperties jwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("secret-key");
        jwtProperties.setHeader("header");
        jwtProperties.setPrefix("prefix");
        jwtProperties.setExpiration(10000);

        return jwtProperties;
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(JwtProperties jwtProperties) {
        return new JwtTokenProvider(jwtProperties);
    }

}
