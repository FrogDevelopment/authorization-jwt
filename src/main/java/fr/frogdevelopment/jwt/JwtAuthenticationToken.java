package fr.frogdevelopment.jwt;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

import io.jsonwebtoken.Claims;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtAuthenticationToken implements Authentication {

    static final String AUTHORITIES_KEY = "authorities";

    private final String principal;
    private final Collection<GrantedAuthority> authorities;
    private final Map<String, Object> details;
    private boolean authenticated;

    JwtAuthenticationToken(Claims claims) {
        this.principal = claims.getSubject();
        //noinspection unchecked
        this.authorities = ((List<String>) claims.getOrDefault(AUTHORITIES_KEY, emptyList()))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(toUnmodifiableList());
        this.details = claims.entrySet()
                .stream()
                .collect(toUnmodifiableMap(Entry::getKey, Entry::getValue));
        this.authenticated = true;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return getPrincipal();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getDetails() {
        return details;
    }

    public Object getDetail(String key) {
        return details.get(key);
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public String getCredentials() {
        return null;
    }

}
