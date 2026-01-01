package _v3r.project.common.util;

import org.springframework.http.ResponseCookie;

public final class CookieUtil {

    private static final String REFRESH_TOKEN_NAME = "refreshToken";

    public static ResponseCookie createRefreshTokenCookie(
            String refreshToken,
            long maxAge
    ) {
        return ResponseCookie.from(REFRESH_TOKEN_NAME, refreshToken)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .build();
    }
    public static ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_NAME, "")
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();
    }
}
