package bootcamp.badgeting.infrastructure.http;

import bootcamp.badgeting.application.DeleteUserUseCase;
import bootcamp.badgeting.application.FindUserUseCase;
import bootcamp.badgeting.domain.user.UserId;
import bootcamp.badgeting.infrastructure.http.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final FindUserUseCase findUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(FindUserUseCase findUserUseCase, DeleteUserUseCase deleteUserUseCase) {
        this.findUserUseCase = findUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getMe(@AuthenticationPrincipal Jwt jwt) {
        return UserResponse.from(findUserUseCase.execute(authenticatedUserId(jwt)));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal Jwt jwt) {
        UserId userId = authenticatedUserId(jwt);
        deleteUserUseCase.execute(userId);
    }

    private UserId authenticatedUserId(Jwt jwt) {
        return new UserId((UUID.fromString(jwt.getSubject())));
    }

}
