package by.brandwatches.authorizationserver.controller;

import by.brandwatches.authorizationserver.service.impl.AuthService;
import by.brandwatches.authorizationserver.service.model.JwtTokens;
import by.brandwatches.authorizationserver.service.model.LoginCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin() {
        // Создайте фиктивные данные
        LoginCredentials credentials = new LoginCredentials("username", "password");
        JwtTokens jwtTokens = new JwtTokens("accessToken", "refreshToken");

        // Мокаем вызов сервиса и задаем ожидаемый результат
        when(authService.auth(credentials)).thenReturn(jwtTokens);

        // Вызываем метод контроллера
        ResponseEntity<JwtTokens> responseEntity = authController.login(credentials);

        // Проверяем, что метод сервиса был вызван с правильными аргументами
        verify(authService).auth(credentials);
        assertSame(jwtTokens, responseEntity.getBody());

    }

    @Test
    void testRefreshTokens() {
        // Создайте фиктивные данные
        JwtTokens jwtTokens = new JwtTokens("accessToken", "refreshToken");

        // Мокаем вызов сервиса и задаем ожидаемый результат
        when(authService.refreshTokens(jwtTokens)).thenReturn(jwtTokens);

        // Вызываем метод контроллера
        ResponseEntity<JwtTokens> responseEntity = authController.refreshTokens(jwtTokens);

        // Проверяем, что метод сервиса был вызван с правильными аргументами
        verify(authService).refreshTokens(jwtTokens);

        // Проверяем, что ответ содержит ожидаемые токены
        assertSame(jwtTokens, responseEntity.getBody());
    }

    @Test
    void testValidateToken() {
        // Создайте фиктивные данные
        String authHeader = "Bearer your-token";

        // Мокаем вызов сервиса и задаем ожидаемый результат
        when(authService.validateToken(authHeader)).thenReturn(true);

        // Вызываем метод контроллера
        ResponseEntity<Boolean> responseEntity = authController.validateToken(authHeader);

        // Проверяем, что метод сервиса был вызван с правильными аргументами
        verify(authService).validateToken(authHeader);

        // Проверяем, что ответ содержит ожидаемое булево значение
        assertEquals(Boolean.TRUE, responseEntity.getBody());

    }
}

