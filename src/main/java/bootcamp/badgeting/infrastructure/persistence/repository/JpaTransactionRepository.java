package bootcamp.badgeting.infrastructure.persistence.repository;

import bootcamp.badgeting.domain.transaction.Category;
import bootcamp.badgeting.domain.transaction.Transaction;
import bootcamp.badgeting.domain.transaction.TransactionId;
import bootcamp.badgeting.domain.transaction.TransactionRepository;
import bootcamp.badgeting.domain.user.UserId;
import bootcamp.badgeting.infrastructure.persistence.entity.TransactionEntity;
import bootcamp.badgeting.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaTransactionRepository implements TransactionRepository {
    private final TransactionEntityRepository repository;
    private final UserEntityRepository userEntityRepository;

    @Override
    public Transaction save(Transaction transaction) {
        UUID ownerId = transaction.getOwnerId().id();
        UserEntity owner = userEntityRepository.findById(ownerId).orElseThrow();

        TransactionEntity entity = TransactionEntity.from(owner, transaction);

        return repository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(UserId owner, Category category) {
        return repository.findAllByOwner_IdAndCategory(owner.id(), category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Transaction> findById(TransactionId id, UserId owner) {
        return repository.findByIdAndOwner_Id(id.id(), owner.id())
                .map(TransactionEntity::toDomain);
    }

    @Override
    public void deleteById(UserId owner, TransactionId id) {
        repository
                .findByIdAndOwner_Id(id.id(), owner.id())
                .ifPresent(repository::delete);
    }


}
