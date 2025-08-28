package _v3r.project.report.domain;

import _v3r.project.common.domain.BaseEntity;
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
        return Report.builder()
                .chat(chat)
                .fluency(response.fluency())
                .persistence(response.persistence())
                .creativity(response.creativity())
                .fluency_s(response.fluencySkc().fluency_s())
                .fluency_k(response.fluencySkc().fluency_k())
                .fluency_c(response.fluencySkc().fluency_c())
                .persistence_s(response.persistenceSrf().persistence_s())
                .persistence_r(response.persistenceSrf().persistence_r())
                .persistence_f(response.persistenceSrf().persistence_f())
                .build();
    }

}
