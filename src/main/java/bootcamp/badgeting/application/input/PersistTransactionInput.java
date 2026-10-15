package bootcamp.badgeting.application.input;

import bootcamp.badgeting.domain.transaction.Category;

public record PersistTransactionInput(String description,
                                      Long amount,
                                      Category category) {
}
