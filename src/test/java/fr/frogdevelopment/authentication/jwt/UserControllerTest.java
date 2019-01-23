package fr.frogdevelopment.authentication.jwt;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ActiveProfiles("test")
@SpringJUnitConfig(JwtApplication.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String URL_TEMPLATE = "/login";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRefuseEmptyLoginWith401() throws Exception {
        // given
        // when
        ResultActions perform = this.mockMvc.perform(
                post(URL_TEMPLATE)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(content().bytes(new byte[0]));
    }

    @Test
    void shouldAcceptLoginAndReturnTheToken() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(
                post(URL_TEMPLATE)
                        .param(SPRING_SECURITY_FORM_USERNAME_KEY, "admin")
                        .param(SPRING_SECURITY_FORM_PASSWORD_KEY, "security_is_fun")
                        .accept(MediaType.APPLICATION_JSON));
        // then
        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertThat(authentication).isNotNull();
    }
}
