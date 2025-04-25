package com.naribackend.infra.auth;

import com.naribackend.core.auth.AccessTokenHandler;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class AccessTokenHandlerImpl implements AccessTokenHandler {

    private final SecretKey secretKey;

    private final long expireSec;

    public AccessTokenHandlerImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expire-sec}") long expireSec)
    {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireSec = expireSec;
    }

    @Override
    public String createTokenBy(Long userId) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expireSec)))
                .signWith(secretKey)
                .compact();
    }


    @Override
    public Long getUserIdFrom(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey)
                    .build().parseSignedClaims(token).getPayload();

            String userIdStr = claims.getSubject();

            return Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            log.error("userId를 Long으로 변경하는 과정에서 에러가 발생하였습니다. 빨리 확인 바랍니다.", e);
            throw new CoreException(ErrorType.AUTHENTICATION_FAIL, e);
        }
        catch (Exception e) {
            throw new CoreException(ErrorType.AUTHENTICATION_FAIL);
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiration.before(new Date());
    }
}
