package bootcamp.badgeting.application;

import bootcamp.badgeting.application.exception.UserNotFoundException;
import bootcamp.badgeting.domain.user.UserId;
import bootcamp.badgeting.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserUseCase {
    private final UserRepository repository;

    public DeleteUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public void execute(UserId id) {
        boolean userExists = repository.findByUserId(id).isPresent();

        if(!userExists) {
            throw new UserNotFoundException();
        }

        repository.delete(id);

    }

}
