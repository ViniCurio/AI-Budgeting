package bootcamp.badgeting.domain.transaction;

import bootcamp.badgeting.domain.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private TransactionId id;
    private UserId ownerId;
    private String description;
    private Long amount;
    private Category category;

    public Transaction(UserId ownerId, String description, long amount, Category category) {
        this.id = new TransactionId();
        this.ownerId = ownerId;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

}
