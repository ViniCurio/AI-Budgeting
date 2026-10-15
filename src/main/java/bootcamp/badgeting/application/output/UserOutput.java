package bootcamp.badgeting.application.output;

import bootcamp.badgeting.domain.user.User;
import java.time.Instant;

public record UserOutput(String id,
                         String role,
                         String name,
                         String email,
                         String phoneNumber,
                         Instant createdAt) {
    public static UserOutput fromUser(User user) {
        return new UserOutput(user.getId().id().toString(),
                user.getRole().toString(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCreatedAt());
    }
}
