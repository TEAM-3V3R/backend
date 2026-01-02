package _v3r.project.user.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.user.dto.request.CreateUserRequest;
import _v3r.project.user.dto.request.UpdateUserRequest;
import _v3r.project.user.dto.response.CreateUserResponse;
import _v3r.project.user.dto.response.FindUserResponse;
import _v3r.project.user.dto.response.UpdateUserResponse;
import _v3r.project.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "유저 컨트롤러", description = "유저 관련 API입니다.")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public CustomApiResponse<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return CustomApiResponse.success(response,200,"회원가입 성공");
    }
    @PutMapping("/update")
    @Operation(summary = "회원정보 수정")
    public CustomApiResponse<UpdateUserResponse> updateUser(@RequestHeader(name = "user-no") Long userId,@RequestBody
            UpdateUserRequest request) {
        UpdateUserResponse response = userService.updateUser(userId, request);
        return CustomApiResponse.success(response,200,"사용자 업데이트 성공");
    }

    @GetMapping("/find")
    @Operation(summary = "회원 조회")
    public CustomApiResponse<FindUserResponse> findUser(@RequestHeader(name = "user-no") Long userId) {
        FindUserResponse response = userService.findUser(userId);
        return CustomApiResponse.success(response,200,"유저 조회 성공");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 삭제")
    public void deleteUser(@RequestHeader(name = "user-no") Long userId) {
        userService.deleteUser(userId);
    }

}
