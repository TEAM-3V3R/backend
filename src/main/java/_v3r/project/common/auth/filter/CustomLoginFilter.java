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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationSec;

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest =
                    objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            String loginId = loginRequest.loginId();
            String password = loginRequest.password();

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginId, password);

            return authenticationManager.authenticate(authRequest);

        } catch (IOException e) {
            //TODO security 전용 예외 핸들러 만들어서 관리필요
            throw new BadCredentialsException("Invalid login request body");
        }
    }


    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {

        CustomUserDetails principal =
                (CustomUserDetails) authResult.getPrincipal();

        Long userId = principal.getUserId();

        String accessToken =
                jwtUtil.createJWT(userId, accessTokenExpirationMs);

        long refreshTokenExpirationMs = refreshTokenExpirationSec * 1000L;
        String refreshToken =
                jwtUtil.createJWT(userId, refreshTokenExpirationMs);

        redisUtil.setDataExpire(
                RedisKeyUtil.refreshToken(userId),
                refreshToken,
                refreshTokenExpirationSec
        );

        res.setHeader(AUTH_HEADER, BEARER + accessToken);

        ResponseCookie cookie =
                CookieUtil.createRefreshTokenCookie(
                        refreshToken,
                        refreshTokenExpirationSec
                );
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        res.setStatus(HttpStatus.OK.value());
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(req, res, failed);
    }
}
