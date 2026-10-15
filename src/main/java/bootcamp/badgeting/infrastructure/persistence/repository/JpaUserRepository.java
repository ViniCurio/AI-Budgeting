package bootcamp.badgeting.infrastructure.persistence.repository;

import bootcamp.badgeting.application.exception.UserNotFoundException;
import bootcamp.badgeting.domain.user.User;
import bootcamp.badgeting.domain.user.UserId;
import bootcamp.badgeting.domain.user.UserRepository;
import bootcamp.badgeting.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {
    private final UserEntityRepository repository;

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.fromUser(user);
        return repository.save(entity).toUserDomain();
    }

    @Override
    public void delete(UserId id) {
        repository.deleteById(id.id());
    }

    public Optional<User> findByUserId(UserId id) {
        return repository.findById(id.id()).map(UserEntity::toUserDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserEntity::toUserDomain);
    }
}
