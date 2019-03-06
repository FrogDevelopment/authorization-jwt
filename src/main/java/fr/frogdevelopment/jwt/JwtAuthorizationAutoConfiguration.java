package fr.frogdevelopment.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAuthorizationAutoConfiguration {

    @Bean
    @ConfigurationProperties("security.jwt.token")
    JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    RetrieveTokenFromRequest retrieveTokenFromRequest() {
        return new RetrieveTokenFromRequest();
    }

    @Bean
    ResolveClaimsFromToken resolveClaimsFromToken() {
        return new ResolveClaimsFromToken(jwtProperties());
    }

    @Bean
    ResolveTokenToAuthentication resolveTokenToAuthentication() {
        return new ResolveTokenToAuthentication(resolveClaimsFromToken(), retrieveTokenFromRequest());
    }

    @Bean
    JwtProcessTokenFilter jwtProcessTokenFilter() {
        return new JwtProcessTokenFilter(resolveTokenToAuthentication());
    }
}
