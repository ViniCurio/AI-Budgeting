# AI-Budgeting — instruções de autenticação, JWT e isolamento de dados

Este documento consolida o roteiro para permitir cadastro, login, JWT e transações personalizadas por usuário.

## Resultado esperado

- Usuário cria uma conta com e-mail e senha.
- Usuário faz login e recebe um JWT.
- O cliente envia Authorization: Bearer <token> nas rotas protegidas.
- O JWT identifica o usuário no claim sub.
- Toda transação pertence a um usuário.
- Nenhum usuário pode ler ou excluir uma transação de outro.

A regra principal é: JWT autentica a chamada; ownerId isola os dados.

## Estrutura de pacotes

    domain/user
      User, UserId, Role, UserRepository
    domain/transaction
      Transaction, TransactionId, TransactionRepository

    application/input
      UserInput, LoginInput, PersistTransactionInput
    application/output
      UserOutput, LoginOutput, TransactionOutput
    application/exception
      InvalidCredentialsException
    application
      SaveUserUseCase, LoginUseCase, PersistTransactionUseCase

    infrastructure/http/request
      UserRequest, LoginRequest, TransactionRequest
    infrastructure/http/response
      UserResponse, AuthResponse, TransactionResponse
    infrastructure/http
      AuthController, UserController, TransactionController
    infrastructure/persistence/entity
      UserEntity, TransactionEntity
    infrastructure/persistence/repository
      adaptadores JPA e repositórios Spring Data
    infrastructure/security
      SecurityConfig, JwtConfig, JwtProperties, JwtService

Request e Response HTTP pertencem à infraestrutura. Input e Output dos casos de uso pertencem à aplicação.

## User e senha

Campos recomendados do usuário:

    id: UUID
    role: USER
    name
    email único
    phoneNumber
    passwordHash
    createdAt: Instant

No construtor de cadastro, a senha já deve ser hash:

    public User(String name, String email, String phoneNumber, String passwordHash) {
        this.id = new UserId();
        this.role = Role.USER;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

Cuidado: se o argumento do construtor se chama password, a atribuição deve usar password. O valor salvo em passwordHash não pode ficar null.

Instant.now() grava em UTC, que é o recomendado. Para exibir data no Brasil:

    instant.atZone(ZoneId.of("America/Sao_Paulo"))

Não gravar UTC-3 no banco. Não retornar password ou passwordHash em UserResponse, AuthResponse ou qualquer resposta HTTP.

## Cadastro com BCrypt

Em SecurityConfig, criar apenas um bean PasswordEncoder:

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

SaveUserUseCase recebe o encoder por injeção:

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SaveUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserOutput execute(UserInput input) {
        String passwordHash = passwordEncoder.encode(input.password());

        User user = new User(
                input.name(),
                input.email(),
                input.phoneNumber(),
                passwordHash
        );

        return UserOutput.fromUser(userRepository.save(user));
    }

Para login:

    passwordEncoder.matches(senhaEnviada, user.getPasswordHash())

Nunca armazenar senha em texto puro e nunca comparar senha com equals.

## Dependências JWT

Adicionar ao build.gradle:

    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
    implementation 'org.springframework.security:spring-security-oauth2-jose'
    testImplementation 'org.springframework.security:spring-security-test'

No application.properties:

    app.jwt.secret=variável de ambiente JWT_SECRET
    app.jwt.issuer=ai-budgeting
    app.jwt.access-token-ttl=PT15M

JWT_SECRET deve ser uma chave aleatória Base64 com no mínimo 32 bytes. Nunca subir a chave real no Git.

JwtProperties:

    @ConfigurationProperties(prefix = "app.jwt")
    public record JwtProperties(String secret, String issuer, Duration accessTokenTtl) {}

## SecurityConfig

A API deve ser STATELESS. Cadastro e login são públicos; todo o resto exige token:

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

A rota pública e o controller precisam coincidir. Se o cadastro continuar em POST /user, ele receberá 401 com a configuração acima. Preferir POST /auth/register.

## JwtConfig e JwtService

JwtConfig precisa de:

    @Configuration
    @EnableConfigurationProperties(JwtProperties.class)

Ela deve criar SecretKey, JwtEncoder e JwtDecoder com HS256. O decoder deve validar o issuer:

    decoder.setJwtValidator(
            JwtValidators.createDefaultWithIssuer(jwtProperties.issuer())
    );

Usar JwtProperties de modo consistente; evitar misturar com @Value sem necessidade.

JwtService cria o token com:

    sub   UUID do usuário
    iss   ai-budgeting
    iat   instante de emissão
    exp   instante de expiração
    roles USER ou ADMIN

Exemplo:

    public String generateToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getId().id().toString())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.accessTokenTtl()))
                .claim("roles", List.of(user.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

## Login

Adicionar busca por e-mail:

    // UserRepository
    Optional<User> findByEmail(String email);

    // UserEntityRepository
    Optional<UserEntity> findByEmail(String email);

O JpaUserRepository converte UserEntity para User de domínio.

Arquivos de login:

    infrastructure/http/request/LoginRequest.java
    application/input/LoginInput.java
    application/output/LoginOutput.java
    infrastructure/http/response/AuthResponse.java

Fluxo:

    LoginRequest -> LoginInput -> LoginUseCase -> LoginOutput -> AuthResponse

LoginRequest:

    public record LoginRequest(String email, String password) {
        public LoginInput toInput() {
            return new LoginInput(email, password);
        }
    }

LoginUseCase deve buscar o usuário, validar senha com matches, criar token com JwtService e devolver LoginOutput.

Para e-mail inexistente e senha inválida, usar a mesma exceção:

    public class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("E-mail ou senha inválidos");
        }
    }

Criar um RestControllerAdvice na infraestrutura para transformar essa exceção em 401 Unauthorized. Não informar se o e-mail existe.

LoginUseCase não deve retornar AuthResponse, pois AuthResponse é infraestrutura. Ele retorna LoginOutput; AuthController faz a conversão.

Endpoints recomendados:

    POST /auth/register
    POST /auth/login
    GET  /users/me
    DELETE /users/me

## Transaction e ownerId

Transaction recebe o UserId no construtor:

    public Transaction(UserId ownerId, String description, long amount, Category category) {
        this.id = new TransactionId();
        this.ownerId = ownerId;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

PersistTransactionInput não recebe ownerId:

    public record PersistTransactionInput(
            String description,
            long amount,
            Category category
    ) {}

PersistTransactionUseCase recebe o usuário separadamente:

    public TransactionOutput execute(UserId ownerId, PersistTransactionInput input) {
        Transaction transaction = new Transaction(
                ownerId,
                input.description(),
                input.amount(),
                input.category()
        );

        return TransactionOutput.from(transactionRepository.save(transaction));
    }

O cliente nunca envia ownerId no body.

## TransactionController

O Spring valida o Bearer token e disponibiliza Jwt como principal. O UUID está no sub:

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @RequestBody TransactionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UserId ownerId = new UserId(UUID.fromString(jwt.getSubject()));

        TransactionOutput transaction = persistTransactionUseCase.execute(
                ownerId,
                request.toInput()
        );

        return TransactionResponse.from(transaction);
    }

O cliente envia:

    Authorization: Bearer <access-token>

Body de exemplo, sem ownerId:

    {
      "description": "Mercado",
      "amount": 15000,
      "category": "GROCERIES"
    }

## TransactionEntity e UserEntity

Para relacionamento JPA com chave estrangeira:

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_transaction_owner")
    )
    private UserEntity owner;

TransactionEntity está na infraestrutura, logo pode receber UserEntity:

    public static TransactionEntity from(UserEntity owner, Transaction transaction) {
        return new TransactionEntity(
                transaction.getId().id(),
                owner,
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory()
        );
    }

Conversão inversa:

    public Transaction toDomain() {
        return new Transaction(
                new TransactionId(this.id),
                new UserId(this.owner.getId()),
                this.description,
                this.amount,
                this.category
        );
    }

JpaTransactionRepository resolve o dono antes de salvar:

    UUID ownerId = transaction.getOwnerId().id();

    UserEntity owner = userEntityRepository.findById(ownerId)
            .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));

    TransactionEntity entity = TransactionEntity.from(owner, transaction);
    return transactionEntityRepository.save(entity).toDomain();

Também é possível usar EntityManager.getReference(UserEntity.class, ownerId).

O trecho incompleto abaixo não compila:

    UserEntity owner =

## Isolamento entre usuários

Não basta JWT na rota. Todas as consultas precisam filtrar por dono:

    findAllByOwnerIdAndCategory(ownerId, category)
    findByIdAndOwnerId(transactionId, ownerId)
    deleteByIdAndOwnerId(transactionId, ownerId)

GET /transactions/{category} e DELETE /transactions/{id} também precisam receber AuthenticationPrincipal Jwt, converter sub para UserId e entregar ao caso de uso.

Isso impede que usuário A consulte ou exclua transações de usuário B.

## Endpoint de IA

POST /transactions/ai também deve exigir JWT.

Não expor uma tool de IA que receba UserId:

    @Tool
    public TransactionOutput execute(UserId ownerId, PersistTransactionInput input)

A IA não pode definir o dono. O owner deve vir do contexto autenticado. Até projetar a propagação segura desse contexto, remover PersistTransactionUseCase de defaultTools.

## Pendências atuais

1. Completar a atribuição de UserEntity owner no JpaTransactionRepository.
2. Filtrar leitura e exclusão de transações por ownerId.
3. Corrigir ou remover a tool de IA que recebe UserId.
4. Alinhar endpoint de cadastro com /auth/register.
5. Remover passwordHash de DTOs de saída.
6. Garantir construtor sem argumentos em entidades JPA.
7. Mapear Role com @Enumerated(EnumType.STRING).
8. Validar campos obrigatórios, e-mail e senha.
9. Retornar 409 Conflict para e-mail duplicado.
10. Testar cadastro, login, JWT inválido/expirado, rotas sem token e isolamento entre dois usuários.

## Ordem de implementação

1. Corrigir User, UserEntity e hash de senha.
2. Concluir JwtConfig, JwtService e SecurityConfig.
3. Implementar POST /auth/register.
4. Implementar busca por e-mail e POST /auth/login.
5. Confirmar que token é emitido e validado.
6. Concluir relação TransactionEntity para UserEntity.
7. Propagar UserId do JWT para todas as operações de transação.
8. Filtrar todas as consultas e exclusões por proprietário.
9. Adaptar o fluxo de IA ao usuário autenticado.
10. Criar testes de segurança e isolamento.

## Status da implementação - 2026-10-15

Esta seção registra o estado revisado do código nesta data e complementa o roteiro acima.

### Implementado

1. Cadastro público em `POST /auth/register`, alinhado com `SecurityConfig`.
2. Login em `POST /auth/login`, com `LoginOutPut` na camada de aplicação e `AuthResponse` convertido no controller.
3. Senhas cadastradas com BCrypt e comparadas com `PasswordEncoder.matches` no login.
4. JWT stateless com HS256, issuer, `sub` contendo o UUID do usuário e expiração configurada.
5. Credenciais inválidas (e-mail inexistente ou senha incorreta) retornam a mesma exceção e HTTP 401.
6. E-mail duplicado retorna HTTP 409; a constraint única no banco foi mantida como proteção final.
7. Validação HTTP adicionada com `@Valid`; cadastro e login validam seus campos e transações validam categoria e valor.
8. `UserEntity.role` usa `@Enumerated(EnumType.STRING)` e as entidades JPA possuem construtor sem argumentos.
9. Os endpoints de usuário foram restritos a `GET /users/me` e `DELETE /users/me`; a identidade vem do `sub` do JWT, não de um ID enviado na URL.
10. Transações recebem o dono pelo JWT no POST, GET por categoria e DELETE. A relação `TransactionEntity -> UserEntity` e a FK foram criadas.
11. Consultas por categoria e exclusão de transação filtram pelo proprietário autenticado.
12. O mapeamento de `TransactionResponse` (descrição e categoria) foi corrigido.

### Pendente antes de expor a funcionalidade de IA

1. Remover `.defaultTools(persistTransactionUseCase, transactionsByCategoryUseCase)` de `TransactionController`.
   A IA ainda não possui uma forma segura de receber o usuário autenticado. Ela nunca deve escolher ou receber `UserId` como parâmetro controlado pelo modelo.

### Correção técnica pendente

1. Em `JpaTransactionRepository.findById`, corrigir a ordem dos argumentos:

       repository.findByIdAndOwner_Id(id.id(), owner.id())

   O contrato de Spring Data é `findByIdAndOwner_Id(transactionId, ownerId)`. A exclusão já usa a ordem correta.

2. Em `TransactionRequest`, acrescentar `@NotBlank` a `description`; atualmente apenas o tamanho máximo é validado, então descrição nula, vazia ou composta apenas por espaços ainda pode passar.

### Adiado por decisão do projeto

1. Conversão de valores em centavos para reais na resposta de transações. O comportamento atual será validado manualmente antes de qualquer alteração.
2. Testes automatizados de cadastro, login, JWT inválido ou expirado, 401 sem token, e-mail duplicado e isolamento entre usuários A/B. Esses testes serão feitos posteriormente.
