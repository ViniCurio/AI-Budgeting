package bootcamp.badgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class ToolCallingIT {
    @Autowired
    OpenAiChatModel openAiChatModel;

    static class MathTools {

        @Tool(description = "soma dois numeros inteiros, a e b")
        public int sum(int a, int b) {
            return a + b;
        }

        @Tool(description = "subrai dois numeros inteiros, a e b")
        public int diff(int a, int b) {
            return a - b;
        }

    }

    @Test
    void should_executeSum_when_prompted() {
        var chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem("Você é um professor de matemática")
                .defaultTools(new MathTools())
                .build();

        var response = chatClient.prompt("some 20 mais 30, depois subtraia 10 do resultado anterior, me traga somente a resposta, sem explicações")
                .call().content();

        assertThat(response).contains("40");
        System.out.println(response);

    }


}
