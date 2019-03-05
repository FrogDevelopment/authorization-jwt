package fr.frogdevelopment.authentication.jwt;

public interface JwtUserDetailsService {

    void addRevokedToken(String jti);

    boolean isRevoked(String jti);
}
