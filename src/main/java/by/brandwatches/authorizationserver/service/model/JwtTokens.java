package by.brandwatches.authorizationserver.service.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtTokens {
    String accessToken;
    String refreshToken;
}
