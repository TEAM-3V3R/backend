package _v3r.project.user.dto.request;
import _v3r.project.user.domain.User;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "ID는 공백일 수 없습니다.")
        String id,
        String nickName) {

        public User toEntity() {
                return User.builder()
                        .id(this.id)
                        .nickname(this.nickName)
                        .build();
        }

}
