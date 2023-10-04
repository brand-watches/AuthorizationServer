package by.brandwatches.authorizationserver.message;

public class Messages {
    // Authentication messages
    public static final String BAD_CREDENTIALS = "Неправильный логин или пароль";

    // JWT messages
    public static final String MALFORMED_JWT = "Invalid JWT token form";
    public static final String EXPIRED_JWT = "Expired JWT token";
    public static final String UNSUPPORTED_JWT = "Unsupported JWT token";
    public static final String JWT_TOKEN_VALIDATION_EXCEPTION = "JWT token is empty or null";
}
