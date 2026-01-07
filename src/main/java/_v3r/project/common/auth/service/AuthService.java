package _v3r.project.common.auth.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.auth.dto.response.AuthResponse;
import _v3r.project.common.exception.EverException;
import _v3r.project.common.util.CookieUtil;
import _v3r.project.common.util.JwtUtil;
import _v3r.project.common.util.RedisKeyUtil;
import _v3r.project.common.util.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public AuthResponse reissue(String oldRefreshToken, HttpServletResponse response) {
        jwtUtil.validateRefreshToken(oldRefreshToken);
        Long userId = jwtUtil.getUserId(oldRefreshToken);
        String redisKey = RedisKeyUtil.refreshToken(userId);
        String storedToken = redisUtil.getData(redisKey);

        if (!Objects.equals(storedToken, oldRefreshToken)) {
            throw new EverException(ErrorCode.TOKEN_MISSING);
        }
        String newAccessToken = jwtUtil.createAccessJWT(userId, accessTokenExpiration);
        String newRefreshToken = jwtUtil.createRefreshJWT(userId, refreshTokenExpiration);
        redisUtil.setDataExpire(
                redisKey,
                newRefreshToken,
                refreshTokenExpiration
        );
        ResponseCookie cookie =
                CookieUtil.createRefreshTokenCookie(
                        newRefreshToken,
                        refreshTokenExpiration
                );

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return AuthResponse.of(newAccessToken);
    }
}
