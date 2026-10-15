package bootcamp.badgeting.infrastructure.persistence.repository;

import bootcamp.badgeting.domain.transaction.Category;
import bootcamp.badgeting.domain.user.UserId;
import bootcamp.badgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByOwner_IdAndCategory(UUID ownerId, Category category);

    Optional<TransactionEntity> findByIdAndOwner_Id(UUID transactionId, UUID ownerId);

}
