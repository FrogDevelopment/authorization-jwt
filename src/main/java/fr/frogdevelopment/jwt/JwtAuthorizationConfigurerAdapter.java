package fr.frogdevelopment.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutFilter;

public abstract class JwtAuthorizationConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtProcessTokenFilter jwtProcessTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF (cross site request forgery)
        http.csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // handle an unauthorized attempts
        http.exceptionHandling()
                .authenticationEntryPoint((req, rsp, e) -> rsp.sendError(
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase())
                );

        // Apply JWT
        http.addFilterBefore(jwtProcessTokenFilter, LogoutFilter.class);

        // Entry points
        http.authorizeRequests()
                // allow access to actuator health api
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();
    }
}
