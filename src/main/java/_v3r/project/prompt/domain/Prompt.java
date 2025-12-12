package _v3r.project.prompt.domain;

import _v3r.project.common.domain.BaseEntity;
import _v3r.project.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "prompt")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Prompt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    private Long promptId;

    @Column(name = "prompt_content")
    private String promptContent;

    // user 끊고 chat과 연결하는게 효율적일듯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "inpainting_type")
    private Boolean inpaintingImage = false;


    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Prompt toEntity(User user, String promptContent,Chat chat) {
        return Prompt.builder()
                .chat(chat)
                .user(user)
                .promptContent(promptContent)
                .build();
    }

    public void updateImage(Boolean inpaintingImage) {
        this.inpaintingImage = inpaintingImage;
    }

}

