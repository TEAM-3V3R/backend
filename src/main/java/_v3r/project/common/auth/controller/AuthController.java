package _v3r.project.common.auth.controller;

import _v3r.project.common.auth.dto.request.LoginRequest;
import _v3r.project.common.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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


}

