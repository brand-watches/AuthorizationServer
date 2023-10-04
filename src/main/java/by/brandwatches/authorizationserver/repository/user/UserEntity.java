package by.brandwatches.authorizationserver.repository.user;


import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "user")
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;
}
