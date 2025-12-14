package _v3r.project.report.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.report.dto.ReportResponse;
import _v3r.project.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@Tag(name = "AI보고서 컨트롤러", description = "AI보고서 관련 API입니다.")
public class ReportController {
    private final ReportService reportService;
    @PostMapping("/{chatId}")
    @Operation(summary = "분석 내용 받기")
    public CustomApiResponse<Void> getReport(
            @RequestHeader("user-no") Long userId,
            @PathVariable("chatId") Long chatId
    ) {
        reportService.receiveReport(userId, chatId);
        return CustomApiResponse.success(null, 200, "리포트 수신 성공");
    }

    @GetMapping("/{chatId}/show")
    @Operation(summary = "ai 보고서 조회")
    public CustomApiResponse<ReportResponse> showReport(
            @RequestHeader("user-no") Long userId,
            @PathVariable("chatId") Long chatId
    ) {
        ReportResponse response = reportService.showReport(userId, chatId);
        return CustomApiResponse.success(response,200,"리포트 조회 성공");
    }


}
