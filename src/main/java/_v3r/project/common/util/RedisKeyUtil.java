package _v3r.project.common.util;

public final class RedisKeyUtil {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    public static String refreshToken(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }
}
