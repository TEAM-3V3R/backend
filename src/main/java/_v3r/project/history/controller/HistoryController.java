package _v3r.project.history.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.auth.model.CustomUserDetails;
import _v3r.project.history.domain.enumType.SortType;
import _v3r.project.history.dto.AllHistoryResponse;
import _v3r.project.history.dto.DetailHistoryResponse;
import _v3r.project.history.service.HistoryService;
import _v3r.project.prompt.domain.enumtype.Paints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/history")
@Tag(name = "히스토리 컨트롤러", description = "히스토리 관련 API입니다.")
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping
    @Operation(summary = "히스토리 조회 기능 - default : 최신순 정렬")
    public CustomApiResponse<List<AllHistoryResponse>> findHistory(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(value = "paints", required = false) Paints paints,
            @RequestParam(value = "sort", defaultValue = "최신순") SortType sortType
    ) {
        List<AllHistoryResponse> response = historyService.findHistory(principal.getUserId(), paints, sortType);
        return CustomApiResponse.success(response, 200, "히스토리 조회 성공");
    }
    @GetMapping("/{chatId}")
    @Operation(summary = "히스토리 세부 조회 기능")
    public  CustomApiResponse<DetailHistoryResponse> detailFindHistory(
            @AuthenticationPrincipal CustomUserDetails principal, @PathVariable("chatId") Long chatId) {

        DetailHistoryResponse response = historyService.detailFindHistory(principal.getUserId(), chatId);
        return CustomApiResponse.success(response,200,"히스토리 세부 내역 조회 성공");

    }


}
