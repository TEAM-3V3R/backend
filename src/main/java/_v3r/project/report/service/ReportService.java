package _v3r.project.report.service;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.report.domain.Report;
import _v3r.project.report.dto.ReportResponse;
import _v3r.project.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final FlaskService flaskService;
    private final ChatRepository chatRepository;

    //TODO 채팅방 종료되면 프론트에서 호출해야함
    @Transactional
    public void receiveReport(Long userId,Long chatId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (!chat.getUser().getUserId().equals(userId)) {
            throw new EverException(ErrorCode.FORBIDDEN);
        }

        CustomApiResponse<ReportResponse> response = flaskService.receiveReport(chatId);

        ReportResponse reportResponse = response.data();

        Report report = Report.toEntity(chat, reportResponse);

        reportRepository.save(report);
    }
    @Transactional(readOnly = true)
    public ReportResponse showReport(Long userId, Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (!chat.getUser().getUserId().equals(userId)) {
            throw new EverException(ErrorCode.FORBIDDEN);
        }
        Report report = reportRepository.findByChat_ChatId(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        return ReportResponse.of(report);
    }
}

