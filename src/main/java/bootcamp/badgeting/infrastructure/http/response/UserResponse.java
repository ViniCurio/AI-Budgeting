package bootcamp.badgeting.infrastructure.http.response;

import bootcamp.badgeting.application.output.UserOutput;
import java.time.Instant;

public record UserResponse(String id,
                           String role,
                           String name,
                           String email,
                           String phoneNumber,
                           Instant createdAt) {
    public static UserResponse from(UserOutput output) {
        return new UserResponse(
                output.id(),
                output.role(),
                output.name(),
                output.email(),
                output.phoneNumber(),
                output.createdAt());
    }
}
