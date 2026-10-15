package bootcamp.badgeting.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UserId id;
    private Role role;
    private String name;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private Instant createdAt;

    public User(String name, String email, String phoneNumber, String passwordHash) {
        this.id = new UserId();
        this.role = Role.USER;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }
}
