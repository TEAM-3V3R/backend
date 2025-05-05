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
    private Long id;

    @Column(name = "prompt_content")
    private String promptContent;

    // user 끊고 chat과 연결하는게 효율적일듯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "category_matching_sum")
    Double categoryMatchingSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public void updateCategoryMatchingSum(double categoryMatchingSum) {
        this.categoryMatchingSum = categoryMatchingSum;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Prompt create(User user, String promptContent,Chat chat) {
        return Prompt.builder()
                .chat(chat)
                .user(user)
                .promptContent(promptContent)
                .build();
    }

}

