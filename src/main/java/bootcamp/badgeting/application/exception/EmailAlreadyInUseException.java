package bootcamp.badgeting.application.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException() {
        super("E-mail já cadastrado!");
    }
}
