package bootcamp.badgeting.infrastructure.http.request;

import bootcamp.badgeting.application.input.UserInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 30, message = "Nome deve ter no máximo 30 caracteres!")
        String name,

        @NotBlank(message = "E-mail e obrigatório!")
        @Email(message = "E-mail inválido!")
        @Size(max = 50, message = "O e-mail deve ter no máximo 50 caracteres!")
        String email,

        @NotBlank(message = "Telefone é obrigatório!")
        @Pattern(regexp = "^(\\+?\\d{10,15})?$", message = "Telefone inválido!")
        String phoneNumber,

        @NotBlank(message = "A senha é obrigatoria!")
        @Size(min = 8, max = 15, message = "A senha deve ter entre 8 e 15 caracteres!")
        String password) {

    public UserInput toInput() {
        return new UserInput(name, email, phoneNumber, password);

    }

}
