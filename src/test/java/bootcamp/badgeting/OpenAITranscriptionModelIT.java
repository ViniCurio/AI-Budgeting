package bootcamp.badgeting;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAITranscriptionModelIT {
    @Autowired
    OpenAiAudioTranscriptionModel  openAiAudioTranscriptionModel;

    @ParameterizedTest
    @CsvSource({
            "teste_1.mp3, 30 reais",
            "teste_2.mp3, 130 reais",
            "teste_3.mp3, 40 reais",
    })
    void should_containExpectedKeyWords_when_audioFilesAreProcessed(String fileName, String expectedKeyWords) {

        var recording = new ClassPathResource("audios/"  + fileName);

        var response = openAiAudioTranscriptionModel.call(recording);

        assertThat(response).contains(expectedKeyWords);
        System.out.println(response);

    }

}
