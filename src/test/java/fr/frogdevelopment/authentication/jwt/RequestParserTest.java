package fr.frogdevelopment.authentication.jwt;

import static fr.frogdevelopment.authentication.jwt.RequestParser.TOKEN_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RunWith(JUnitPlatform.class)
class RequestParserTest {

    private RequestParser requestParser = new RequestParser();

    @Test
    void should_return_null_when_missing_authorization() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("BAD HEADER", UUID.randomUUID().toString());

        // when
        String token = requestParser.retrieveToken(request);

        // then
        assertNull(token);
    }

    @Test
    void should_return_null_when_wrong_header_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "WRONG TOKEN");

        // when
        String token = requestParser.retrieveToken(request);

        // then
        assertNull(token);
    }


    @Test
    void should_return_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String uuid = UUID.randomUUID().toString();
        request.addHeader(AUTHORIZATION, TOKEN_TYPE + uuid);

        // when
        String token = requestParser.retrieveToken(request);

        // then
        assertNotNull(token);
        assertEquals(uuid, token);
    }
}
