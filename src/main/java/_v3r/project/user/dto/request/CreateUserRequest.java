package _v3r.project.user.dto.request;
import _v3r.project.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public record CreateUserRequest(
        @NotBlank(message = "ID는 공백일 수 없습니다.")
        String id,
        String nickName,
        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자리로 입력해 주세요")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-\\[\\]{};':\"\\\\|,.<>\\/?]{8,20}$",
                message = "비밀번호는 영문자 숫자를 포함해야 합니다."
        )
        String password) {

        public User toEntity(String encodedPassword) {
                return User.builder()
                        .loginId(this.id)
                        .nickname(this.nickName)
                        .password(encodedPassword)
                        .build();
        }

}
