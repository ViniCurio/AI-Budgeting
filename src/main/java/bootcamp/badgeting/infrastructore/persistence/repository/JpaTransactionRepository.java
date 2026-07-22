package bootcamp.badgeting.infrastructore.persistence.repository;

import bootcamp.badgeting.domain.Category;
import bootcamp.badgeting.domain.Transaction;
import bootcamp.badgeting.domain.TransactionId;
import bootcamp.badgeting.domain.TransactionRepository;
import bootcamp.badgeting.infrastructore.persistence.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaTransactionRepository implements TransactionRepository {
    private final TransactionEntityRepository repository;

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);
        return repository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return repository.findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return repository.findById(id.id()).map(TransactionEntity::toDomain);
    }

    @Override
    public void deleteById(TransactionId id) {
        repository.deleteById(id.id());
    }


}
