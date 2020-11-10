package com.frogdevelopment.jwt.app.conf;

import com.frogdevelopment.jwt.JwtAuthorizationConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class AuthenticationConfiguration extends JwtAuthorizationConfigurerAdapter {

}
