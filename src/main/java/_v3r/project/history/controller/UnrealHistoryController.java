package _v3r.project.history.controller;


import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.history.dto.UnrealHistoryResponse;
import _v3r.project.history.service.UnrealHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/unreal-history")
@Tag(name = "후처리 히스토리 컨트롤러", description = "후처리 히스토리 관련 API입니다.")
public class UnrealHistoryController {

    private final UnrealHistoryService unrealHistoryService;

    @PostMapping("/{chatId}")
    @Operation(summary = "언리얼 통신 후 데이터 저장 기능")
    public CustomApiResponse<Void> saveUnrealHistory(
            @RequestBody UnrealHistoryResponse request, @RequestHeader("user-no") Long userId,@PathVariable("chatId") Long chatId) {
        unrealHistoryService.saveUnrealHistory(userId,chatId ,request);
        return CustomApiResponse.success(null,200,"후처리 히스토리 저장 완료");
    }


    @GetMapping("/{chatId}")
    @Operation(summary = "후처리 히스토리 조회 기능")
    public CustomApiResponse<List<UnrealHistoryResponse>> showUnrealHistory(
            @RequestHeader("user-no") Long userId, @PathVariable("chatId") Long chatId
    ) {
        List<UnrealHistoryResponse> response = unrealHistoryService.showUnrealHistory(userId, chatId);
        return CustomApiResponse.success(response, 200, "후처리 히스토리 조회 성공");
    }
}
