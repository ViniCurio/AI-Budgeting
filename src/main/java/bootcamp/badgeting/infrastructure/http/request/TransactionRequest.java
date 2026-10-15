package bootcamp.badgeting.infrastructure.http.request;

import bootcamp.badgeting.application.input.PersistTransactionInput;
import bootcamp.badgeting.domain.transaction.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransactionRequest(

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String description,

        @NotNull(message = "A categoria e obrigatório!")
        Category category,

        @NotNull(message = "O valor é obrigatório!")
        @Positive(message = "O valor deve ser maior do que zero!")
        Long amount

) {

    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description(), amount, category);
    }

}
