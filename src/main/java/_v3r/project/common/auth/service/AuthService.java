package _v3r.project.common.auth.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.auth.dto.request.LoginRequest;
import _v3r.project.common.auth.dto.response.LoginResponse;
import _v3r.project.common.exception.EverException;
import _v3r.project.common.util.JwtUtil;
import _v3r.project.common.util.RedisKeyUtil;
import _v3r.project.common.util.RedisUtil;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    //TODO 로그인
    @Transactional
    public LoginResponse login(LoginRequest req) {

        User user = userRepository.findByLoginId(req.loginId())
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new EverException(ErrorCode.INVALID_PASSWORD);
        }
        String accessToken = jwtUtil.createJWT(user.getUserId(), accessTokenExpiration);
        String refreshToken = jwtUtil.createJWT(user.getUserId(), refreshTokenExpiration);

        redisUtil.setDataExpire(RedisKeyUtil.refreshToken(user.getUserId()),
                refreshToken,
                refreshTokenExpiration);

        return LoginResponse.of(accessToken);
    }
}
