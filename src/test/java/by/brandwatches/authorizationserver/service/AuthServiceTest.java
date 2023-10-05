package by.brandwatches.authorizationserver.service;

import by.brandwatches.authorizationserver.exception.BadCredentialsException;
import by.brandwatches.authorizationserver.repository.user.UserEntity;
import by.brandwatches.authorizationserver.repository.user.UserRepository;
import by.brandwatches.authorizationserver.security.provider.JwtProvider;
import by.brandwatches.authorizationserver.security.provider.JwtSecretEnum;
import by.brandwatches.authorizationserver.service.impl.AuthService;
import by.brandwatches.authorizationserver.service.model.JwtTokens;
import by.brandwatches.authorizationserver.service.model.LoginCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuth_ValidCredentials() {
        // Создаем фиктивные данные
        LoginCredentials credentials = new LoginCredentials("username", "password");
        UserEntity user = new UserEntity();
        user.setLogin("username");
        user.setPassword("$2a$10$Y6sXLtv1hBcnXVXRxynEBeW2hkFOHdj47ZlaAiJ53z6SKN41iGHqa");

        JwtTokens expectedTokens = new JwtTokens("accessToken", "refreshToken");

        // Мокаем вызовы UserRepository и JwtProvider
        when(userRepository.findByLogin(credentials.getLogin())).thenReturn(user);
        when(jwtProvider.getTokens(user)).thenReturn(expectedTokens);

        // Мокируем вызов matches

        // Вызываем метод auth
        JwtTokens actualTokens = authService.auth(credentials);

        // Проверяем, что результат содержит ожидаемые токены
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void testAuth_InvalidCredentials() {
        // Создаем фиктивные данные
        LoginCredentials credentials = new LoginCredentials("username", "incorrectPassword");
        UserEntity user = new UserEntity();
        user.setLogin("username");
        user.setPassword("$2a$10$Y6sXLtv1hBcnXVXRxynEBeW2hkFOHdj47ZlaAiJ53z6SKN41iGHqa");

        // Мокаем вызов UserRepository
        when(userRepository.findByLogin(credentials.getLogin())).thenReturn(user);

        // Вызываем метод auth и ожидаем исключение BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> authService.auth(credentials));
    }

    @Test
    void testRefreshTokens_ValidTokens() {
        // Создаем фиктивные данные
        JwtTokens tokens = new JwtTokens("accessToken", "refreshToken");
        String loginFromToken = "username";
        UserEntity user = new UserEntity();
        user.setLogin(loginFromToken);

        JwtTokens expectedTokens = new JwtTokens("newAccessToken", "newRefreshToken");

        // Мокаем вызовы JwtProvider и UserRepository
        when(jwtProvider.validateToken(tokens.getRefreshToken(), JwtSecretEnum.REFRESH_SECRET)).thenReturn(true);
        when(jwtProvider.getLoginFromToken(tokens.getRefreshToken(), JwtSecretEnum.REFRESH_SECRET)).thenReturn(loginFromToken);
        when(userRepository.findByLogin(loginFromToken)).thenReturn(user);
        when(jwtProvider.getTokens(user)).thenReturn(expectedTokens);

        // Вызываем метод refreshTokens
        JwtTokens actualTokens = authService.refreshTokens(tokens);

        // Проверяем, что результат содержит ожидаемые токены
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void testRefreshTokens_InvalidTokens() {
        // Создаем фиктивные данные
        JwtTokens tokens = new JwtTokens("accessToken", "invalidRefreshToken");

        // Мокаем вызов JwtProvider и ожидаем исключение BadCredentialsException
        when(jwtProvider.validateToken(tokens.getRefreshToken(), JwtSecretEnum.REFRESH_SECRET)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.refreshTokens(tokens));
    }

    @Test
    void testValidateToken_ValidToken() {
        // Создаем фиктивные данные
        String authHeader = "Bearer validToken";

        // Мокаем вызов JwtProvider
        when(jwtProvider.extractToken(authHeader)).thenReturn("validToken");
        when(jwtProvider.validateToken("validToken", JwtSecretEnum.ACCESS_SECRET)).thenReturn(true);

        // Вызываем метод validateToken
        boolean isValid = authService.validateToken(authHeader);

        // Проверяем, что токен был признан действительным
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Создаем фиктивные данные
        String authHeader = "Bearer invalidToken";

        // Мокаем вызов JwtProvider и ожидаем, что токен будет признан недействительным
        when(jwtProvider.extractToken(authHeader)).thenReturn("invalidToken");
        when(jwtProvider.validateToken("invalidToken", JwtSecretEnum.ACCESS_SECRET)).thenReturn(false);

        // Вызываем метод validateToken
        boolean isValid = authService.validateToken(authHeader);

        // Проверяем, что токен был признан недействительным
        assertFalse(isValid);
    }
}
