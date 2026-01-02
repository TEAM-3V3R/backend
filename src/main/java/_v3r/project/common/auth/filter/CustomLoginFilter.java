package _v3r.project.common.auth.filter;

import _v3r.project.common.auth.dto.request.LoginRequest;
import _v3r.project.common.auth.model.CustomUserDetails;
import _v3r.project.common.util.CookieUtil;
import _v3r.project.common.util.JwtUtil;
import _v3r.project.common.util.RedisKeyUtil;
import _v3r.project.common.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationSec;

    public CustomLoginFilter(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            RedisUtil redisUtil,
            long accessTokenExpirationSec,
            long refreshTokenExpirationSec
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
        this.accessTokenExpirationMs = accessTokenExpirationSec * 1000L;
        this.refreshTokenExpirationSec = refreshTokenExpirationSec;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest =
                    objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.loginId(),
                            loginRequest.password()
                    );

            return authenticationManager.authenticate(authRequest);

        } catch (IOException e) {
            log.warn("[로그인 실패] Invalid request body", e);
            throw new BadCredentialsException("Invalid login request body");
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {

        CustomUserDetails principal =
                (CustomUserDetails) authResult.getPrincipal();

        Long userId = principal.getUserId();

        String accessToken =
                jwtUtil.createJWT(userId, accessTokenExpirationMs);

        String refreshToken =
                jwtUtil.createJWT(userId, refreshTokenExpirationSec * 1000L);

        redisUtil.setDataExpire(
                RedisKeyUtil.refreshToken(userId),
                refreshToken,
                refreshTokenExpirationSec
        );

        response.setHeader(AUTH_HEADER, BEARER + accessToken);

        ResponseCookie cookie =
                CookieUtil.createRefreshTokenCookie(
                        refreshToken,
                        refreshTokenExpirationSec
                );
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }
}
