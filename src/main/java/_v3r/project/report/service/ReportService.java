package _v3r.project.report.service;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.report.domain.Report;
import _v3r.project.report.dto.ReportResponse;
import _v3r.project.report.repository.ReportRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final FlaskService flaskService;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportResponse receiveReport(Long userId,Long chatId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));


        CustomApiResponse<ReportResponse> response = flaskService.receiveReport(chatId);

        ReportResponse reportResponse = response.data();

        Report report = Report.toEntity(chat, reportResponse);

        reportRepository.save(report);
        return reportResponse;

    }
}

