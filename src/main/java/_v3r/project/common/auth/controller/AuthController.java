package _v3r.project.common.auth.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.auth.dto.request.LoginRequest;
import _v3r.project.common.auth.dto.response.AuthResponse;
import _v3r.project.common.auth.service.AuthService;
import _v3r.project.common.exception.EverException;
import jakarta.servlet.http.Cookie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "로그인 컨트롤러", description = "로그인 관련 API입니다.")
public class AuthController {

    private final AuthService authService;
    //TODO 스웨거 테스트용 로그인 컨트롤러라 실 서비스에선 수정 필요
    @Operation(
            summary = "로그인",
            description = "loginId와 password로 로그인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/login")
    public void login(@RequestBody LoginRequest request) {
        throw new UnsupportedOperationException("Handled by Spring Security Filter");
    }
    @PostMapping("/reissue")
    public ResponseEntity<CustomApiResponse<AuthResponse>> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken = Arrays.stream(
                        Optional.ofNullable(request.getCookies())
                                .orElseThrow(() ->
                                        new EverException(ErrorCode.TOKEN_MISSING)
                                )
                )
                .filter(c -> c.getName().equals("refreshToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() ->
                        new EverException(ErrorCode.TOKEN_MISSING)
                );

        AuthResponse result =
                authService.reissue(refreshToken, response);

        return ResponseEntity.ok()
                .body(CustomApiResponse.success(
                        result,
                        200,
                        "토큰 재발급 성공"
                ));
    }



}

