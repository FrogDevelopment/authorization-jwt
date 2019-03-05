package fr.frogdevelopment.authentication.jwt.handler;

import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
abstract class AbstractJwtAuthenticationSuccessHandlerTest {

    @Mock
    protected HttpServletRequest httpServletRequest;
    @Mock
    protected HttpServletResponse httpServletResponse;
    @Mock
    protected Authentication authentication;
    @Mock
    protected PrintWriter writer;

    @BeforeEach
    void beforeEach() throws IOException {
        given(httpServletResponse.getWriter()).willReturn(writer);
    }

}
