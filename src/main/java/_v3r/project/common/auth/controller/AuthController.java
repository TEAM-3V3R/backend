package _v3r.project.common.auth.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.auth.dto.request.LoginRequest;
import _v3r.project.common.auth.dto.response.LoginResponse;
import _v3r.project.common.auth.service.AuthService;
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

    @PostMapping("/login")
    public CustomApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return CustomApiResponse.success(response, 200, "로그인 성공");
    }

}

