package bootcamp.badgeting.application;

import bootcamp.badgeting.application.exception.InvalidCredentialsException;
import bootcamp.badgeting.application.input.LoginInput;
import bootcamp.badgeting.application.output.LoginOutPut;
import bootcamp.badgeting.domain.user.User;
import bootcamp.badgeting.domain.user.UserRepository;
import bootcamp.badgeting.infrastructure.security.JwtProperties;
import bootcamp.badgeting.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public LoginUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtProperties jwtProperties
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public LoginOutPut execute(LoginInput input) {
        User user = userRepository.findByEmail(input.email())
                .orElseThrow(InvalidCredentialsException::new);

        boolean validPassword = passwordEncoder.matches(
                input.password(),
                user.getPasswordHash()
        );

        if (!validPassword) {
            throw new InvalidCredentialsException();
        }

        return new LoginOutPut(
                jwtService.generateToken(user),
                "Bearer",
                jwtProperties.accessTokenTtl().toSeconds()
        );
    }
}
