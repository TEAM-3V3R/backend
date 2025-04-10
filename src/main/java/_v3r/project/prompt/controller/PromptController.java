package _v3r.project.prompt.controller;

import _v3r.project.common.annotation.AuthUser;
import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.prompt.dto.PromptRequest;
import _v3r.project.prompt.dto.PromptResponse;
import _v3r.project.prompt.service.PromptService;
import _v3r.project.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prompt")
public class PromptController {

    private final PromptService promptService;
    @PostMapping("/send-prompt")
    public CustomApiResponse<PromptResponse> sendPrompt(
            @RequestHeader("user-no")Long userId,
            @RequestBody PromptRequest request) {
        PromptResponse response = promptService.sendPrompt(userId, request);
        return CustomApiResponse.success(response, 200, "프롬프트 요청 및 저장 완료");
    }

}
