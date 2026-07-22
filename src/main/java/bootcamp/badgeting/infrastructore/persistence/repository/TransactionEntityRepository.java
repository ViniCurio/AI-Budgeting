package bootcamp.badgeting.infrastructore.persistence.repository;

import bootcamp.badgeting.domain.Category;
import bootcamp.badgeting.infrastructore.persistence.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);
}
