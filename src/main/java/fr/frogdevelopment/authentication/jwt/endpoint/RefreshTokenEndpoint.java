package fr.frogdevelopment.authentication.jwt.endpoint;

import fr.frogdevelopment.authentication.jwt.JwtTokenProvider;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnWebApplication
@ConditionalOnBean({UserDetailsService.class})
public class RefreshTokenEndpoint {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public RefreshTokenEndpoint(JwtTokenProvider jwtTokenProvider,
                                UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "auth/token/refresh", produces = {MediaType.TEXT_PLAIN_VALUE})
    public String refreshToken(HttpServletRequest request) {

        String username = jwtTokenProvider.refreshToken(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return jwtTokenProvider.createAccessToken(userDetails);
    }
}
