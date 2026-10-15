package bootcamp.badgeting.application.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException () {
        super ("E-mail ou Senha inválidos");
    }
}
