package bootcamp.badgeting.infrastructure.persistence.entity;

import bootcamp.badgeting.domain.transaction.Transaction;
import bootcamp.badgeting.domain.transaction.TransactionId;
import bootcamp.badgeting.domain.user.Role;
import bootcamp.badgeting.domain.user.User;
import bootcamp.badgeting.domain.user.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    private String passwordHash;

    private Instant createdAt;


    public static UserEntity fromUser(User user) {
        return new UserEntity(user.getId().id(),
                user.getRole(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPasswordHash(),
                user.getCreatedAt()
        );
    }

    public User toUserDomain() {
        return new User(
                new UserId(this.id),
                this.role,
                this.name,
                this.email,
                this.phoneNumber,
                this.passwordHash,
                this.createdAt
        );
    }

}
