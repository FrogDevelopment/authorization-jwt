package fr.frogdevelopment.authentication.jwt;

import static fr.frogdevelopment.authentication.jwt.TokenProvider.AUTHORITIES_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RunWith(JUnitPlatform.class)
class TokenProviderTest {

    private static final String ISSUER = "test";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final Set<String> AUTHORITIES = Set.of("ADMIN", "USER");
    private static final String SECRET_KEY = "SECRET_KEY";

    private TokenProvider tokenProvider;

    private final JwtProperties jwtProperties = new JwtProperties();

    @BeforeEach
    void beforeEach() {
        jwtProperties.setSecretKey(SECRET_KEY);
        tokenProvider = new TokenProvider(ISSUER, jwtProperties);
    }

    @Test
    void shouldReturnTokenWhenCreatingAccessTokenFromUserDetails() {
        // given
        var userDetails = givenUserDetails();

        // when
        var token = tokenProvider.createAccessToken(userDetails);

        // then
        thenAccessTokenIsCreatedWithAllNeededData(token);
    }

    @Test
    void shouldReturnTokenWhenCreatingAccessTokenFromAuthentication() {
        // given
        var authentication = givenAuthentication();

        // when
        var token = tokenProvider.createAccessToken(authentication);

        // then
        thenAccessTokenIsCreatedWithAllNeededData(token);
    }

    @Test
    void shouldReturnTokenWhenCreatingRefreshToken() {
        // given
        var authentication = givenAuthentication();

        // when
        var token = tokenProvider.createRefreshToken(authentication);

        // then
        thenRefreshTokenIsCreatedWithAllNeededData(token);
    }

    @NotNull
    private UserDetails givenUserDetails() {
        var grantedAuthorities = AUTHORITIES.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new User(USERNAME, PASSWORD, grantedAuthorities);
    }

    @NotNull
    private UsernamePasswordAuthenticationToken givenAuthentication() {
        var grantedAuthorities = AUTHORITIES.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, grantedAuthorities);
    }

    private void thenAccessTokenIsCreatedWithAllNeededData(Token token) {
        assertThat(token).isNotNull();
        assertThat(token.getId()).isNull();
        assertThat(token.getIssuer()).isEqualTo(ISSUER);
        assertThat(token.getSubject()).isEqualTo(USERNAME);
        assertThat(token.getClaims()).containsEntry(AUTHORITIES_KEY, AUTHORITIES);
        assertThat(token.getExpiration()).isEqualTo(jwtProperties.getAccessTokenExpiration());
        assertThat(token.getSecretKey()).isEqualTo(SECRET_KEY);
    }

    private void thenRefreshTokenIsCreatedWithAllNeededData(Token token) {
        assertThat(token).isNotNull();
        assertThat(token.getId()).isNotBlank();
        assertThat(token.getIssuer()).isEqualTo(ISSUER);
        assertThat(token.getSubject()).isEqualTo(USERNAME);
        assertThat(token.getClaims()).isNull();
        assertThat(token.getExpiration()).isEqualTo(jwtProperties.getRefreshTokenExpiration());
        assertThat(token.getSecretKey()).isEqualTo(SECRET_KEY);
    }

}
