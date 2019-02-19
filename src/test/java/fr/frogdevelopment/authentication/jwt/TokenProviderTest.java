package fr.frogdevelopment.authentication.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.frogdevelopment.authentication.jwt.conf.JwtApplication;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ActiveProfiles("test")
@SpringJUnitConfig(JwtApplication.class)
@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void createAccessToken() {
        // given
        var username = "USERNAME";
        var grantedAuthorities = List.of("ADMIN", "USER").stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        var authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);

        // when
        var token = tokenProvider.createAccessToken(authentication);

        // then
        assertNotNull(token);
    }

}
