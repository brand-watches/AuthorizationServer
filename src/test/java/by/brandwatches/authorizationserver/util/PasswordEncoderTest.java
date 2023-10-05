package by.brandwatches.authorizationserver.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncoderTest {


    @Test
    void testMatchPassword_ValidPassword() {
        // Ожидаемый пароль
        String rawPassword = "password";
        String encodedPassword = "$2a$10$Y6sXLtv1hBcnXVXRxynEBeW2hkFOHdj47ZlaAiJ53z6SKN41iGHqa";

        // Вызываем статический метод matchPassword
        boolean result = PasswordEncoder.matchPassword(rawPassword, encodedPassword);

        // Проверяем, что пароли совпадают
        assertTrue(result);
    }

    @Test
    void testMatchPassword_InvalidPassword() {
        // Ожидаемый пароль
        String rawPassword = "password";
        String encodedPassword = "hashedPassword";

        // Вызываем статический метод matchPassword
        boolean result = PasswordEncoder.matchPassword(rawPassword, encodedPassword);

        // Проверяем, что пароли не совпадают
        assertFalse(result);
    }
}
