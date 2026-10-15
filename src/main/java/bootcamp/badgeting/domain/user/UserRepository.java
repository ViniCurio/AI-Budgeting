package bootcamp.badgeting.domain.user;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    void delete(UserId id);
    Optional<User> findByUserId(UserId id);
    Optional<User> findByEmail(String email);
}
