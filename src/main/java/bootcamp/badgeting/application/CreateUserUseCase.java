package bootcamp.badgeting.application;

import bootcamp.badgeting.application.exception.EmailAlreadyInUseException;
import bootcamp.badgeting.application.input.UserInput;
import bootcamp.badgeting.application.output.UserOutput;
import bootcamp.badgeting.domain.user.User;
import bootcamp.badgeting.domain.user.UserRepository;
import jakarta.validation.constraints.Email;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserOutput execute(UserInput input) {
        boolean emailExists = userRepository.findByEmail(input.email()).isPresent();

        if(emailExists) {
            throw new EmailAlreadyInUseException();
        }

        String passwordHash = passwordEncoder.encode(input.password());
        var user = userRepository.save(
                new User(input.name(), input.email(), input.phoneNumber(), passwordHash));

        return UserOutput.fromUser(user);

    }

}
