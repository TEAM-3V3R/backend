package _v3r.project.user.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.user.dto.request.LoginUserRequest;
import _v3r.project.user.dto.request.LogoutUserRequest;
import _v3r.project.user.dto.response.LoginUserResponse;
import _v3r.project.user.service.AuthService;
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
    public CustomApiResponse<LoginUserResponse> login(@RequestBody LoginUserRequest request) {
        LoginUserResponse response = authService.login(request);
        return CustomApiResponse.success(response, 200, "로그인 성공");
    }

    @PostMapping("/logout")
    public CustomApiResponse<String> logout(@RequestBody LogoutUserRequest request) {
        authService.logout(request.idName());
        return CustomApiResponse.success("로그아웃 성공", 200, "로그아웃 완료");
    }

}

