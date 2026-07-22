package bootcamp.badgeting.infrastructore.http;

import bootcamp.badgeting.application.DeleteTransactionUseCase;
import bootcamp.badgeting.application.PersistTransactionUseCase;
import bootcamp.badgeting.application.TransactionsByCategoryUseCase;
import bootcamp.badgeting.domain.Category;
import bootcamp.badgeting.domain.TransactionId;
import bootcamp.badgeting.infrastructore.http.request.TransactionRequest;
import bootcamp.badgeting.infrastructore.http.response.TransactionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final TransactionsByCategoryUseCase transactionsByCategoryUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 TransactionsByCategoryUseCase transactionsByCategoryUseCase,
                                 DeleteTransactionUseCase deleteTransactionUseCase,
                                 TranscriptionModel transcriptionModel,
                                 ChatClient.Builder chatClientBuilder,
                                 @Value("classpath:/prompts/system-message.st") Resource systemPrompt,
                                 TextToSpeechModel textToSpeechModel) throws IOException {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.transactionsByCategoryUseCase = transactionsByCategoryUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistTransactionUseCase, transactionsByCategoryUseCase)
                .build();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> readTransactions(@PathVariable Category category) {
        return transactionsByCategoryUseCase.execute(category)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        var userMessage = transcriptionModel.transcribe(file.getResource());

        var result = chatClient.prompt().user(userMessage).call().content();

        textToSpeechModel.call(result);

        byte[] audio = textToSpeechModel.call(result);
        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .attachment()
                                .filename("audio.mp3")
                                .build()
                                .toString())
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable UUID id) {
        deleteTransactionUseCase.execute(new TransactionId(id));
    }

}
