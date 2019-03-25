package fr.frogdevelopment.jwt.app.conf;

import fr.frogdevelopment.jwt.JwtAuthorizationConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class AuthenticationConfiguration extends JwtAuthorizationConfigurerAdapter {

}
