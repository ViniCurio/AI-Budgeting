package bootcamp.badgeting.application;

import bootcamp.badgeting.application.input.PersistTransactionInput;
import bootcamp.badgeting.application.output.TransactionOutput;
import bootcamp.badgeting.domain.transaction.Transaction;
import bootcamp.badgeting.domain.transaction.TransactionRepository;
import bootcamp.badgeting.domain.user.UserId;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionOutput execute(UserId owner, PersistTransactionInput input) {
        var transaction = transactionRepository.save(
                new Transaction(owner, input.description(), input.amount(), input.category()));

        return TransactionOutput.from(transaction);

    }

}
