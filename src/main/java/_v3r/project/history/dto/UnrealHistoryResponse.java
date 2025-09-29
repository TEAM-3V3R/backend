package _v3r.project.history.dto;

import _v3r.project.history.domain.UnrealHistory;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.user.domain.User;
import java.time.Instant;
import lombok.Builder;

@Builder
public record UnrealHistoryResponse(
        Long userId,
        int chatId,
        String actorName,
        String uuid,
        String changeType,
        Instant timestamp
) {
    public static UnrealHistoryResponse of(UnrealHistory unrealHistory) {
        return UnrealHistoryResponse.builder()
                .userId(unrealHistory.getUser().getUserId())
                .chatId(unrealHistory.getChat().getChatId().intValue())
                .actorName(unrealHistory.getActorName())
                .uuid(unrealHistory.getUuid())
                .changeType(unrealHistory.getChangeType())
                .timestamp(unrealHistory.getTimestamp())
                .build();
    }

    public UnrealHistory toEntity(User user, Chat chat) {
        return UnrealHistory.builder()
                .user(user)
                .chat(chat)
                .actorName(this.actorName)
                .uuid(this.uuid)
                .changeType(this.changeType)
                .timestamp(this.timestamp)
                .user(user)
                .build();
    }

}
