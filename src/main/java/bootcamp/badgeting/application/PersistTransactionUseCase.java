package bootcamp.badgeting.application;

import bootcamp.badgeting.application.input.PersistTransactionInput;
import bootcamp.badgeting.application.output.TransactionOutput;
import bootcamp.badgeting.domain.Transaction;
import bootcamp.badgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "persist-transaction", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(@ToolParam(description = "Dados a serem persistidos") PersistTransactionInput input) {
        var transaction = transactionRepository.save(
                new Transaction(input.description(), input.amount(), input.category()));

        return TransactionOutput.from(transaction);

    }

}
