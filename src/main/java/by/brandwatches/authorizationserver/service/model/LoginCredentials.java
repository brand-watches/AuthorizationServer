package by.brandwatches.authorizationserver.service.model;

import lombok.Data;
import lombok.Getter;

@Data
public class LoginCredentials {
    String login;
    String password;
}
