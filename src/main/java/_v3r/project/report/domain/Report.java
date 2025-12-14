package _v3r.project.report.domain;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.domain.BaseEntity;
import _v3r.project.common.exception.EverException;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.report.dto.ReportResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "report")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "fluency")
    private Float fluency;

    @Column(name = "persistence")
    private Float persistence;

    @Column(name = "creativity")
    private Float creativity;

    @Column(name = "fluency_s")
    private Float fluency_s;

    @Column(name = "fluency_k")
    private Float fluency_k;

    @Column(name = "fluency_c")
    private Float fluency_c;

    @Column(name = "persistence_s")
    private Float persistence_s;

    @Column(name = "persistence_r")
    private Float persistence_r;

    @Column(name = "persistence_f")
    private Float persistence_f;

    @OneToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public static Report toEntity(Chat chat, ReportResponse response) {

        List<Float> fluencySkc = response.fluencySkc();
        List<Float> persistenceSrf = response.persistenceSrf();

        if (fluencySkc == null || fluencySkc.size() != 3
                || persistenceSrf == null || persistenceSrf.size() != 3) {
            throw new EverException(ErrorCode.BAD_REQUEST);
        }

        return Report.builder()
                .chat(chat)
                .fluency(response.fluency())
                .persistence(response.persistence())
                .creativity(response.creativity())
                .fluency_s(fluencySkc.get(0))
                .fluency_k(fluencySkc.get(1))
                .fluency_c(fluencySkc.get(2))
                .persistence_s(persistenceSrf.get(0))
                .persistence_r(persistenceSrf.get(1))
                .persistence_f(persistenceSrf.get(2))
                .build();
    }



}
