package bootcamp.badgeting.application;

import bootcamp.badgeting.application.output.TransactionOutput;
import bootcamp.badgeting.domain.transaction.Category;
import bootcamp.badgeting.domain.transaction.TransactionRepository;
import bootcamp.badgeting.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionsByCategoryUseCase {
    private final TransactionRepository repository;

    public List<TransactionOutput> execute(UserId ownerId, Category category) {
        return repository.findAllByCategory(ownerId ,category)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }

}
