package fr.frogdevelopment.authentication.jwt;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.frogdevelopment.authentication.jwt.conf.JwtApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@ActiveProfiles("test")
@SpringJUnitConfig(JwtApplication.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTest {

    private static final String URL_LOGIN = "/login";
    private static final String URL_LOGOUT = "/logout";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn401WhenLoginWithBadCredentials() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = post(URL_LOGIN)
                .param(SPRING_SECURITY_FORM_USERNAME_KEY, "user")
                .param(SPRING_SECURITY_FORM_PASSWORD_KEY, "bla bla bla")
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions resultActions = this.mockMvc.perform(requestBuilder);

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldReturn200WithTokenWhenLoginWithGoodCredentials() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = post(URL_LOGIN)
                .param(SPRING_SECURITY_FORM_USERNAME_KEY, "admin")
                .param(SPRING_SECURITY_FORM_PASSWORD_KEY, "security_is_fun")
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions resultActions = this.mockMvc.perform(requestBuilder);
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.expirationDate").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    void shouldReturn200WhenLogout() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = post(URL_LOGOUT)
                .accept(MediaType.APPLICATION_JSON);

        // when
        ResultActions resultActions = this.mockMvc.perform(requestBuilder);

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
