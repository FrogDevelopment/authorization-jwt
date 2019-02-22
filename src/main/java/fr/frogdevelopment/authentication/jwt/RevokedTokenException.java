package fr.frogdevelopment.authentication.jwt;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(FORBIDDEN)
public class RevokedTokenException extends RuntimeException {

}
