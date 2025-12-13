package _v3r.project.report.dto;

import _v3r.project.report.domain.Report;
import lombok.Builder;

/** fluency : 유연성 최종 점수
 * persistence : 지속성 최종 점수
 * creativity : ai 보고서 최종 점수
 * */
@Builder
public record ReportResponse(
        Float fluency,
        Float persistence,
        Float creativity,
        FluencySKC fluencySkc,
        PersistenceSRF persistenceSrf
) {
    // flask 응답 구조때문에 객체 사용으로 고정
    public record FluencySKC(
            Float fluency_s,
            Float fluency_k,
            Float fluency_c
    ) {

    }

    public record PersistenceSRF(
            Float persistence_s,
            Float persistence_r,
            Float persistence_f
    ) {

    }
    public static ReportResponse of(Report report) {
        return ReportResponse.builder()
                .fluency(report.getFluency())
                .persistence(report.getPersistence())
                .creativity(report.getCreativity())
                .fluencySkc(new FluencySKC(
                        report.getFluency_s(),
                        report.getFluency_k(),
                        report.getFluency_c()
                ))
                .persistenceSrf(new PersistenceSRF(
                        report.getPersistence_s(),
                        report.getPersistence_r(),
                        report.getPersistence_f()
                ))
                .build();
    }
}