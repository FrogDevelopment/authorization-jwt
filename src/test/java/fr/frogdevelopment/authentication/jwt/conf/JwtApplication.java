package fr.frogdevelopment.authentication.jwt.conf;

import fr.frogdevelopment.authentication.jwt.JwtUserDetailsService;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"fr.frogdevelopment.authentication.jwt"})
public class JwtApplication {

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource,
                                                       @Value("classpath:sql/script_create_tables.sql") Resource scriptCreate,
                                                       @Value("classpath:sql/insert_users.sql") Resource scriptInsert) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(scriptCreate, scriptInsert));

        return dataSourceInitializer;
    }

    @Bean
    public JwtUserDetailsService jwtUserDetailsService() {
        return new JwtUserDetailsService() {
            private final Set<String> tokens = new HashSet<>();

            @Override
            public void addRevokedToken(String jti) {
                tokens.add(jti);
            }

            @Override
            public boolean isRevoked(String jti) {
                return tokens.contains(jti);
            }
        };
    }
}
