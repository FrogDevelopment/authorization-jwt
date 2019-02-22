package fr.frogdevelopment.authentication.jwt;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;

    JwtAuthenticationToken(String principal, Collection<SimpleGrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

}
