package bootcamp.badgeting.infrastructure.persistence.entity;

import bootcamp.badgeting.domain.transaction.Category;
import bootcamp.badgeting.domain.transaction.Transaction;
import bootcamp.badgeting.domain.transaction.TransactionId;
import bootcamp.badgeting.domain.user.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ownerID",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_transaction_owner")
    )
    private UserEntity owner;

    private String description;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private Category category;


    public static TransactionEntity from(UserEntity owner, Transaction transaction) {
        return new TransactionEntity(transaction.getId().id(),
                owner,
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory()
                );
    }

    public Transaction toDomain() {
        return new Transaction(
                new TransactionId(this.id),
                new UserId(this.owner.getId()),
                this.description,
                this.amount,
                this.category
        );
    }

}
