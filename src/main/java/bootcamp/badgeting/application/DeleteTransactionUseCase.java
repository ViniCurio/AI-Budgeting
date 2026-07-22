package bootcamp.badgeting.application;

import bootcamp.badgeting.domain.TransactionId;
import bootcamp.badgeting.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {
    private final TransactionRepository repository;

    public void execute(TransactionId id) {
        repository.deleteById(id);
    }
}
