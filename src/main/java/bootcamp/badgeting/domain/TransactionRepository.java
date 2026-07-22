package bootcamp.badgeting.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);
    List<Transaction> findAllByCategory(Category category);
    Optional<Transaction> findById(TransactionId id);
    void deleteById(TransactionId id);

}
