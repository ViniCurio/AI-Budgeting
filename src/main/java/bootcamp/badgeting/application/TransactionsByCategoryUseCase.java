package bootcamp.badgeting.application;

import bootcamp.badgeting.application.output.TransactionOutput;
import bootcamp.badgeting.domain.Category;
import bootcamp.badgeting.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionsByCategoryUseCase {
    private final TransactionRepository repository;

    @Tool(name = "list-transactions-by-category", description = "Lista transações financeiras por categoria")
    public List<TransactionOutput> execute(@ToolParam(description = "Categoria de uma transação") Category category) {
        return repository.findAllByCategory(category)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }

}
