package by.brandwatches.authorizationserver.security.provider;

import by.brandwatches.authorizationserver.message.Messages;
import by.brandwatches.authorizationserver.repository.user.UserEntity;
import by.brandwatches.authorizationserver.service.model.JwtTokens;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
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

    private final String nameFieldLogin = "login";

    private final String nameFieldId = "id";

    private Map<JwtSecretEnum, String> secretKeyMap;

    @PostConstruct
    public void init() {
        secretKeyMap = new HashMap<>();
        secretKeyMap.put(JwtSecretEnum.ACCESS_SECRET, jwtAccessSecret);
        secretKeyMap.put(JwtSecretEnum.REFRESH_SECRET, jwtRefreshSecret);
    }
    public JwtTokens getTokens(UserEntity user) {
        String accessToken = getJwtToken(user, this.jwtAccessSecret, this.jwtAccessTokenExp);
        String refreshToken = getJwtToken(user, this.jwtRefreshSecret, this.jwtRefreshTokenExp);
        return new JwtTokens(accessToken, refreshToken);
    }

    public boolean validateToken(String token, JwtSecretEnum jwtSecret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(this.secretKeyMap.get(jwtSecret)))
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

    public String getLoginFromToken(String token, JwtSecretEnum jwtSecret) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSecretKey(this.secretKeyMap.get(jwtSecret)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get(this.nameFieldLogin, String.class);
    }


    private String getJwtToken(UserEntity user, String jwtSecretKey, Long jwtExp) {
        SecretKey secretKey = getSecretKey(jwtSecretKey);

        Map<String, Object> fields = new HashMap<>();

        fields.put(this.nameFieldId, user.getId());
        fields.put(this.nameFieldLogin, user.getLogin());

        return Jwts
                .builder()
                .setClaims(fields)
                .setSubject(user.getLogin())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExp))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    private SecretKey getSecretKey(String secretKey) {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }
}
