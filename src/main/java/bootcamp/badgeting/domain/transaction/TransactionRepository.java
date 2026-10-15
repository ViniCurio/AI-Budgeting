package bootcamp.badgeting.domain.transaction;

import bootcamp.badgeting.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);
    List<Transaction> findAllByCategory(UserId owner, Category category);
    Optional<Transaction> findById(TransactionId id, UserId owner);
    void deleteById(UserId owner, TransactionId id);

}
