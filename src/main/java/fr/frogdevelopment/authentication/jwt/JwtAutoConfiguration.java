package fr.frogdevelopment.authentication.jwt;

import fr.frogdevelopment.authentication.jwt.endpoint.RefreshTokenEndpoint;
import fr.frogdevelopment.authentication.jwt.handler.JwtAuthenticationLogoutSuccessHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class JwtAutoConfiguration {

    @Bean
    @ConditionalOnBean({JwtUserDetailsService.class})
    RefreshTokenVerifier refreshTokenVerifier(JwtUserDetailsService jwtUserDetailsService) {
        return new RefreshTokenVerifier(jwtUserDetailsService);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnBean({JwtUserDetailsService.class})
    RefreshTokenEndpoint refreshTokenEndpoint(JwtParser jwtParser,
                                              TokenProvider tokenProvider,
                                              UserDetailsService userDetailsService) {
        return new RefreshTokenEndpoint(jwtParser, tokenProvider, userDetailsService);
    }

    @Bean
    @ConditionalOnBean({JwtUserDetailsService.class})
    JwtAuthenticationLogoutSuccessHandler jwtAuthenticationLogoutSuccessHandler(JwtParser jwtParser,
                                                                                JwtUserDetailsService jwtUserDetailsService) {
        return new JwtAuthenticationLogoutSuccessHandler(jwtParser, jwtUserDetailsService);
    }

}
