package bootcamp.badgeting.infrastructure.http.response;

import bootcamp.badgeting.application.output.TransactionOutput;

public record TransactionResponse(
        String id,
        String description,
        String category,
        double amount) {

    public static TransactionResponse from(TransactionOutput output) {
        return new TransactionResponse(output.id(), output.description(), output.category(), output.value());
    }
}
