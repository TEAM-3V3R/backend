package _v3r.project.prompt.domain;

import _v3r.project.common.domain.BaseEntity;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Chat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "paints_type")
    private Paints paints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_finished")
    private Boolean isFinished = false;

    @Column(name = "chat_title")
    private String chatTitle;

    public void updateChat(Boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void updateChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }
    public static Chat toEntity(User user, Paints paints) {
        return Chat.builder()
                .user(user)
                .paints(paints)
                .isFinished(false)
                .build();
    }


}
