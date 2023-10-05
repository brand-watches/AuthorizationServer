package by.brandwatches.authorizationserver.controller;

import by.brandwatches.authorizationserver.service.impl.AuthService;
import by.brandwatches.authorizationserver.service.model.JwtTokens;
import by.brandwatches.authorizationserver.service.model.LoginCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<JwtTokens> login(@RequestBody LoginCredentials credentials) {
        return ResponseEntity.ok(authService.auth(credentials));
    }

    @PostMapping("/refresh")
    @ResponseBody
    public ResponseEntity<JwtTokens> refreshTokens(@RequestBody JwtTokens tokens) {
        return ResponseEntity.ok(authService.refreshTokens(tokens));
    }

    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.validateToken(authHeader));
    }

}
