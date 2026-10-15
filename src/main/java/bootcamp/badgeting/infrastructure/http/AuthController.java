package bootcamp.badgeting.infrastructure.http;

import bootcamp.badgeting.application.LoginUseCase;
import bootcamp.badgeting.application.CreateUserUseCase;
import bootcamp.badgeting.application.output.LoginOutPut;
import bootcamp.badgeting.infrastructure.http.request.LoginRequest;
import bootcamp.badgeting.infrastructure.http.request.UserRequest;
import bootcamp.badgeting.infrastructure.http.response.AuthResponse;
import bootcamp.badgeting.infrastructure.http.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final CreateUserUseCase createUserUseCase;

    public AuthController(LoginUseCase loginUseCase, CreateUserUseCase createUserUseCase) {
        this.loginUseCase = loginUseCase;
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse saveUser(@Valid @RequestBody UserRequest request) {
        var user = createUserUseCase.execute(request.toInput());
        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) throws Exception {
        LoginOutPut login = loginUseCase.execute(request.toInput());
        return AuthResponse.from(login);
    }
}
