package bootcamp.badgeting.application.input;

import bootcamp.badgeting.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

public record PersistTransactionInput(@ToolParam(description = "Descrição do gasto")String description,
                                      @ToolParam(description = "Valor gasto(em centavos)") long amount,
                                      @ToolParam(description = "Categoria de uma transação") Category category) {
}
