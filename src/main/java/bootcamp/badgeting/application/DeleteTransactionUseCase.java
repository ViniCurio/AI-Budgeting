package bootcamp.badgeting.application;

import bootcamp.badgeting.domain.transaction.TransactionId;
import bootcamp.badgeting.domain.transaction.TransactionRepository;
import bootcamp.badgeting.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {
    private final TransactionRepository repository;

    public void execute(UserId ownerId, TransactionId id) {
        repository.deleteById(ownerId, id);
    }
}
