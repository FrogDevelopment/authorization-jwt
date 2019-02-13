package fr.frogdevelopment.authentication.jwt;

import fr.frogdevelopment.authentication.jwt.filter.JwtLoginFilter;
import fr.frogdevelopment.authentication.jwt.filter.JwtProcessTokenFilter;
import fr.frogdevelopment.authentication.jwt.handler.JwtAuthenticationFailureHandler;
import fr.frogdevelopment.authentication.jwt.handler.JwtAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public abstract class JwtSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_ENTRY_POINT = "/login";
    private static final String LOGOUT_ENTRY_POINT = "/logout";
    private static final String TOKEN_REFRESH_ENTRY_POINT = "auth/token/refresh";

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JwtAuthenticationSuccessHandler successHandler;
    @Autowired
    private JwtAuthenticationFailureHandler failureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF (cross site request forgery)
        http.csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // handle an unauthorized attempts
        http.exceptionHandling()
                .authenticationEntryPoint((req, rsp, e) -> rsp.sendError(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase())
                );

        // Apply JWT
        http.addFilterBefore(new JwtProcessTokenFilter(jwtTokenProvider), LogoutFilter.class);

        // Entry points
        http.authorizeRequests()
                // allow access to actuator health endpoint
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .antMatchers(LOGIN_ENTRY_POINT).permitAll()
                .antMatchers(LOGOUT_ENTRY_POINT).permitAll()
                .antMatchers(TOKEN_REFRESH_ENTRY_POINT).permitAll()
                // others have to be at least authenticated
                .anyRequest().authenticated();
    }

    protected final void configureLogout(HttpSecurity http) throws Exception {
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_ENTRY_POINT, HttpMethod.POST.name()))
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    protected final void configureLogin(HttpSecurity http) throws Exception {
        var loginFilter = new JwtLoginFilter(successHandler, failureHandler);
        loginFilter.setAuthenticationManager(authenticationManager());

        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
