package _v3r.project.common.auth.exception;

import _v3r.project.common.apiResponse.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler
        implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        log.warn(
                "[LOGIN FAIL] ip={}, reason={}",
                request.getRemoteAddr(),
                exception.getClass().getSimpleName()
        );
        CustomApiResponse.errorResponse(
                response,
                "아이디 또는 비밀번호가 올바르지 않습니다.",
                HttpStatus.UNAUTHORIZED.value()
        );
    }
}
