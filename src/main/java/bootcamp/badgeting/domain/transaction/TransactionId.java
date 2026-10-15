package bootcamp.badgeting.domain.transaction;

import java.util.UUID;

public record TransactionId(UUID id) {
    public TransactionId() {
        this(UUID.randomUUID());
    }
}
