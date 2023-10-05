package by.brandwatches.authorizationserver.service.impl;

import by.brandwatches.authorizationserver.exception.BadCredentialsException;
import by.brandwatches.authorizationserver.message.Messages;
import by.brandwatches.authorizationserver.security.provider.JwtProvider;
import by.brandwatches.authorizationserver.repository.user.UserEntity;
import by.brandwatches.authorizationserver.repository.user.UserRepository;
import by.brandwatches.authorizationserver.security.provider.JwtSecretEnum;
import by.brandwatches.authorizationserver.service.IAuthService;
import by.brandwatches.authorizationserver.service.model.JwtTokens;
import by.brandwatches.authorizationserver.service.model.LoginCredentials;
import by.brandwatches.authorizationserver.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService implements IAuthService{
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public JwtTokens auth(LoginCredentials credentials) {
        UserEntity user = userRepository.findByLogin(credentials.getLogin());
        if (Objects.isNull(user)) {
            throw new BadCredentialsException(Messages.BAD_CREDENTIALS);
        }
        if (PasswordEncoder.matchPassword(credentials.getPassword(), user.getPassword())) {
            return jwtProvider.getTokens(user);
        } else {
            throw new BadCredentialsException(Messages.BAD_CREDENTIALS);
        }
    }

    @Override
    public JwtTokens refreshTokens(JwtTokens tokens) {
        if (jwtProvider.validateToken(tokens.getRefreshToken(), JwtSecretEnum.REFRESH_SECRET)) {
            String login = jwtProvider.getLoginFromToken(tokens.getRefreshToken(), JwtSecretEnum.REFRESH_SECRET);
            UserEntity user = userRepository.findByLogin(login);
            return jwtProvider.getTokens(user);
        } else {
            throw new BadCredentialsException(Messages.BAD_CREDENTIALS);
        }
    }

    @Override
    public boolean validateToken(String authHeader) {
        String token = jwtProvider.extractToken(authHeader);
        return jwtProvider.validateToken(token, JwtSecretEnum.ACCESS_SECRET);
    }

}
