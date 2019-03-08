package fr.frogdevelopment.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAuthorizationAutoConfiguration {

    private final String signingKey;

    public JwtAuthorizationAutoConfiguration(@Value("${security.jwt.token.signing-key}") String signingKey) {
        this.signingKey = signingKey;
    }

    @Bean
    RetrieveTokenFromRequest retrieveTokenFromRequest() {
        return new RetrieveTokenFromRequest();
    }

    @Bean
    ResolveClaimsFromToken resolveClaimsFromToken() {
        return new ResolveClaimsFromToken(signingKey);
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
