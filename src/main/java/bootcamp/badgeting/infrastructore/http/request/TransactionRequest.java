package bootcamp.badgeting.infrastructore.http.request;

import bootcamp.badgeting.application.input.PersistTransactionInput;
import bootcamp.badgeting.domain.Category;

public record TransactionRequest(String description, Category category, long amount) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description(), amount, category);
    }
}
