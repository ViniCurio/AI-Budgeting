package bootcamp.badgeting.application;

import bootcamp.badgeting.application.exception.UserNotFoundException;
import bootcamp.badgeting.application.output.UserOutput;
import bootcamp.badgeting.domain.user.UserId;
import bootcamp.badgeting.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindUserUseCase {
    public final UserRepository repository;

    public FindUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public UserOutput execute(UserId id) {
        return repository.findByUserId(id)
                .map(UserOutput::fromUser)
                .orElseThrow(UserNotFoundException::new);
    }
}
