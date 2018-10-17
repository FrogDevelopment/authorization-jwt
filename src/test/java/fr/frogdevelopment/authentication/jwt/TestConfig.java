package fr.frogdevelopment.authentication.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TestConfig {

    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties("secret-key");
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(JwtProperties jwtProperties) {
        return new JwtTokenProvider(jwtProperties);
    }

}
