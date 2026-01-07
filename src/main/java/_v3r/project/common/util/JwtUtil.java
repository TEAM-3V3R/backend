package _v3r.project.common.util;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }
    public String createAccessJWT(Long userId, long expirationSec) {

        Date now = new Date();

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("tokenType", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(
                        new Date(now.getTime() + expirationSec * 1000L)
                )
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshJWT(Long userId, long expirationSec) {

        Date now = new Date();

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("tokenType", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(
                        new Date(now.getTime() + expirationSec * 1000L)
                )
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new EverException(ErrorCode.TOKEN_EXPIRED);
        } catch (SecurityException e) {
            throw new EverException(ErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (JwtException | IllegalArgumentException e) {
            throw new EverException(ErrorCode.TOKEN_INVALID);
        }
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validateRefreshToken(String token) {
        Claims claims = parseClaims(token);

        if (!"REFRESH".equals(claims.get("tokenType", String.class))) {
            throw new EverException(ErrorCode.TOKEN_INVALID);
        }
    }

}
