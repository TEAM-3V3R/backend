package _v3r.project.report.dto;

public record ReportResponse(
        Float fluency,
        Float persistence,
        Float creativity,
        FluencySKC fluencySkc,
        PersistenceSRF persistenceSrf
) {

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
}