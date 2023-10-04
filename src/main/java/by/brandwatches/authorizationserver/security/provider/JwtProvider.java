package by.brandwatches.authorizationserver.security.provider;

import by.brandwatches.authorizationserver.message.Messages;
import by.brandwatches.authorizationserver.repository.user.UserEntity;
import by.brandwatches.authorizationserver.service.model.JwtTokens;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    @Value("${app.jwt.accessKey}")
    private String jwtAccessSecret;

    @Value("${app.jwt.refreshKey}")
    private String jwtRefreshSecret;

    @Value("${app.jwt.exp.accessToken}")
    private Long jwtAccessTokenExp;

    @Value("${app.jwt.exp.refreshToken}")
    private Long jwtRefreshTokenExp;

    public JwtTokens getTokens(UserEntity user) {
        String accessToken = getJwtToken(user, jwtAccessSecret, jwtAccessTokenExp);
        String refreshToken = getJwtToken(user, jwtRefreshSecret, jwtRefreshTokenExp);
        return new JwtTokens(accessToken, refreshToken);
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtAccessSecret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new MalformedJwtException(Messages.MALFORMED_JWT);
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), Messages.EXPIRED_JWT);
        } catch (UnsupportedJwtException ex) {
            throw new UnsupportedJwtException(Messages.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(Messages.JWT_TOKEN_VALIDATION_EXCEPTION);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtRefreshSecret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new MalformedJwtException(Messages.MALFORMED_JWT);
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), Messages.EXPIRED_JWT);
        } catch (UnsupportedJwtException ex) {
            throw new UnsupportedJwtException(Messages.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(Messages.JWT_TOKEN_VALIDATION_EXCEPTION);
        }
    }


    private String getJwtToken(UserEntity user, String jwtSecretKey, Long jwtExp) {
        SecretKey secretKey = getSecretKey(jwtSecretKey);

        Map<String, Object> fields = new HashMap<>();

        fields.put("id", user.getId());
        fields.put("username", user.getUsername());

        return Jwts
                .builder()
                .setClaims(fields)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExp))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    private SecretKey getSecretKey(String secretKey) {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }
}
