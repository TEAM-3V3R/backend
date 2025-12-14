package _v3r.project.report.dto;

import _v3r.project.report.domain.Report;
import java.util.List;
import lombok.Builder;

@Builder
public record ReportResponse(
        Float fluency,
        Float persistence,
        Float creativity,
        List<Float> fluencySkc,
        List<Float> persistenceSrf
) {

    public static ReportResponse of(Report report) {
        return ReportResponse.builder()
                .fluency(report.getFluency())
                .persistence(report.getPersistence())
                .creativity(report.getCreativity())
                .fluencySkc(List.of(
                        report.getFluency_s(),
                        report.getFluency_k(),
                        report.getFluency_c()
                ))
                .persistenceSrf(List.of(
                        report.getPersistence_s(),
                        report.getPersistence_r(),
                        report.getPersistence_f()
                ))
                .build();
    }
}
