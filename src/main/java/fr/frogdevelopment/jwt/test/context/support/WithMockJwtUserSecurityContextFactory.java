package fr.frogdevelopment.jwt.test.context.support;

import static fr.frogdevelopment.jwt.JwtAuthenticationToken.AUTHORITIES_KEY;

import fr.frogdevelopment.jwt.JwtAuthenticationToken;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public final class WithMockJwtUserSecurityContextFactory implements WithSecurityContextFactory<WithMockJwtUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockJwtUser withUser) {
        if (withUser.username() == null || withUser.username().strip().length() == 0) {
            throw new IllegalArgumentException("Username required");
        }

        var claims = new DefaultClaims();
        claims.setSubject(withUser.username());
        claims.put(AUTHORITIES_KEY, withUser.roles());
        for (var claim : withUser.claims()) {
            claims.put(claim.name(), claim.value());
        }

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new JwtAuthenticationToken(claims));
        return context;
    }
}
