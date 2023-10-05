package by.brandwatches.authorizationserver.service;


import by.brandwatches.authorizationserver.service.model.JwtTokens;
import by.brandwatches.authorizationserver.service.model.LoginCredentials;

public interface IAuthService {

    JwtTokens auth(LoginCredentials loginCredentials);

    JwtTokens refreshTokens(JwtTokens tokens);

    boolean validateToken(String authHeader);
}
