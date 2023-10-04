package by.brandwatches.authorizationserver.controller;

import by.brandwatches.authorizationserver.service.impl.AuthService;
import by.brandwatches.authorizationserver.service.model.JwtTokens;
import by.brandwatches.authorizationserver.service.model.LoginCredentials;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping("/login")
    public JwtTokens login(@RequestBody LoginCredentials credentials) {
        return authService.auth(credentials);
    }

    @RequestMapping("/refresh")
    public JwtTokens refreshTokens(@RequestBody JwtTokens tokens) {
        return authService.refreshTokens(tokens);
    }

}
