package com.frogdevelopment.jwt;

import static com.frogdevelopment.jwt.RetrieveTokenFromRequest.TOKEN_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

@Tag("unitTest")
@RunWith(JUnitPlatform.class)
class RetrieveTokenFromRequestTest {

    private RetrieveTokenFromRequest retrieveTokenFromRequest = new RetrieveTokenFromRequest();

    @Test
    void should_return_null_when_missing_authorization() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("BAD HEADER", UUID.randomUUID().toString());

        // when
        String token = retrieveTokenFromRequest.call(request);

        // then
        assertNull(token);
    }

    @Test
    void should_return_null_when_wrong_header_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "WRONG TOKEN");

        // when
        String token = retrieveTokenFromRequest.call(request);

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
        String token = retrieveTokenFromRequest.call(request);

        // then
        assertNotNull(token);
        assertEquals(uuid, token);
    }
}
