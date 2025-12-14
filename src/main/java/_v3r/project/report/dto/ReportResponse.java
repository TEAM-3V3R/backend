package _v3r.project.report.dto;

import _v3r.project.report.domain.Report;
import java.util.List;
import lombok.Builder;

@Builder
public record ReportResponse(
        Float fluency,
        Float persistence,
        Float creativity,
        List<FluencySKC> fluencySkc,
        List<PersistenceSRF> persistenceSrf
) {

    public record FluencySKC(
            Float fluency_s,
            Float fluency_k,
            Float fluency_c
    ) {}

    public record PersistenceSRF(
            Float persistence_s,
            Float persistence_r,
            Float persistence_f
    ) {}

    public static ReportResponse of(Report report) {
        return ReportResponse.builder()
                .fluency(report.getFluency())
                .persistence(report.getPersistence())
                .creativity(report.getCreativity())
                .fluencySkc(List.of(
                        new FluencySKC(
                                report.getFluency_s(),
                                report.getFluency_k(),
                                report.getFluency_c()
                        )
                ))
                .persistenceSrf(List.of(
                        new PersistenceSRF(
                                report.getPersistence_s(),
                                report.getPersistence_r(),
                                report.getPersistence_f()
                        )
                ))
                .build();
    }
}
