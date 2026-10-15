package bootcamp.badgeting.infrastructure.http.request;

import bootcamp.badgeting.application.input.LoginInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "O e-mail é obrigatorio!")
        @Email(message = "E-mail inválido!")
        String email,

        @NotBlank(message = "A senha é obrigatoria!")
        String password
) {

    public LoginInput toInput() {
        return new LoginInput(email, password);
    }

}
