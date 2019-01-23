package fr.frogdevelopment.authentication.jwt;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public abstract class JwtSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF (cross site request forgery)
        http.csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // handle an unauthorized attempts
        http.exceptionHandling()
                .authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // Apply JWT
        http.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), LogoutFilter.class);

        // Entry points
        http.authorizeRequests()
                // allow access to actuator health endpoint
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                // others have to be at least authenticated
                .anyRequest().authenticated();
    }

    protected final void configureLogout(HttpSecurity http) throws Exception {
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.POST.name()))
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    protected final void configureLogin(HttpSecurity http) throws Exception {
        http.addFilterAfter(new JwtLoginFilter(authenticationManager(), jwtTokenProvider), LogoutFilter.class);
    }
}
