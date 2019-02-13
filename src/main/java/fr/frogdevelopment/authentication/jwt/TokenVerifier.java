package fr.frogdevelopment.authentication.jwt;

import org.springframework.stereotype.Component;

@Component
public class TokenVerifier {

    public void verify(String jti) {
//        fixme implement revoked tokens checking (by logout)
//        throw new org.springframework.security.authentication.CredentialsExpiredException("");
    }
}
