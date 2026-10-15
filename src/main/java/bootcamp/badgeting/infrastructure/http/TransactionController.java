package bootcamp.badgeting.infrastructure.http;

import bootcamp.badgeting.application.DeleteTransactionUseCase;
import bootcamp.badgeting.application.PersistTransactionUseCase;
import bootcamp.badgeting.application.TransactionsByCategoryUseCase;
import bootcamp.badgeting.domain.transaction.Category;
import bootcamp.badgeting.domain.transaction.TransactionId;
import bootcamp.badgeting.domain.user.UserId;
import bootcamp.badgeting.infrastructure.http.request.TransactionRequest;
import bootcamp.badgeting.infrastructure.http.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
                .build();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal Jwt jwt) {
        var transaction = persistTransactionUseCase.execute(
                authenticatedUserId(jwt),
                request.toInput()
        );

        return TransactionResponse.from(transaction);

    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> readTransactions(@PathVariable Category category, @AuthenticationPrincipal Jwt jwt) {
        return transactionsByCategoryUseCase.execute(authenticatedUserId(jwt), category)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        deleteTransactionUseCase.execute(authenticatedUserId(jwt), new TransactionId(id));
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

    private UserId authenticatedUserId(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }

}
