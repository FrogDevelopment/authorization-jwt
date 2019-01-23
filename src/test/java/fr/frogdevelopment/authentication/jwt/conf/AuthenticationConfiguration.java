package fr.frogdevelopment.authentication.jwt.conf;

import fr.frogdevelopment.authentication.jwt.JwtSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
public class AuthenticationConfiguration extends JwtSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        configureLogin(http);

        configureLogout(http);
    }

}
