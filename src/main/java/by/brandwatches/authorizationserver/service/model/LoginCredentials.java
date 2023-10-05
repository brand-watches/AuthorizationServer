package by.brandwatches.authorizationserver.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class LoginCredentials {
    String login;
    String password;
}
