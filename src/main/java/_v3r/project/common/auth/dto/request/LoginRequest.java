package _v3r.project.common.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull
        String loginId,
        @NotNull
        String password
) {

}
