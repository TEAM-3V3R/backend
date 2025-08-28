package _v3r.project.report.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.report.dto.ReportResponse;
import _v3r.project.report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@Tag(name = "채팅방 컨트롤러", description = "채팅방 관련 API입니다.")
public class ReportController {
    private final ReportService reportService;
    @GetMapping("/{chatId}")
    public CustomApiResponse<ReportResponse> getReport(
            @RequestHeader("user-no") Long userId,
            @PathVariable("chatId") Long chatId
    ) {
        ReportResponse response = reportService.receiveReport(userId, chatId);
        return CustomApiResponse.success(response, 200, "리포트 수신 성공");
    }


}
