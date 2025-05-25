package _v3r.project.user.dto.request;
import _v3r.project.user.domain.User;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        String name) {

        public User toEntity() {
                return User.builder()
                        .name(this.name)
                        .build();
        }

}
