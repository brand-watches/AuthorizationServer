package by.brandwatches.authorizationserver.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokens {
    String accessToken;
    String refreshToken;
}
