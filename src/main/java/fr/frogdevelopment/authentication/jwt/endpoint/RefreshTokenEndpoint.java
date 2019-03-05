package fr.frogdevelopment.authentication.jwt.endpoint;

import fr.frogdevelopment.authentication.jwt.JwtParser;
import fr.frogdevelopment.authentication.jwt.TokenProvider;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RefreshTokenEndpoint {

    private final JwtParser jwtParser;
    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public RefreshTokenEndpoint(JwtParser jwtParser,
                                TokenProvider tokenProvider,
                                UserDetailsService userDetailsService) {
        this.jwtParser = jwtParser;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "auth/token/refresh", produces = {MediaType.TEXT_PLAIN_VALUE})
    public String refreshToken(HttpServletRequest request) {

        String username = jwtParser.getUsernameFromRefreshToken(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return tokenProvider.createAccessToken(userDetails).toJwt();
    }
}
