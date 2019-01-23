package fr.frogdevelopment.authentication.jwt.conf;

import fr.frogdevelopment.authentication.jwt.JwtLoginFilter;
import fr.frogdevelopment.authentication.jwt.JwtSecurityConfigurerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
public class JwtSecurityConfiguration extends JwtSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        // Logout configuration
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.POST.name()))
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        // Login Filter
        http.addFilterAfter(new JwtLoginFilter(authenticationManager(), jwtTokenProvider()), LogoutFilter.class);

        // Entry points
        http.authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
