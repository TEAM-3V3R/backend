package _v3r.project.user.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.user.dto.request.CreateUserRequest;
import _v3r.project.user.dto.response.CreateUserResponse;
import _v3r.project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public CustomApiResponse<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return CustomApiResponse.success(response,200,"사용자 등록 성공");
    }


}
