# Proxy Pattern PoC — Gerenciamento Dinâmico de Bean (Spring Boot)

Este projeto é uma prova de conceito em Java + Spring Boot que demonstra o uso do padrão estrutural "Proxy" para gerenciar dinamicamente a instância (delegate) de um bean. A PoC mostra como criar um proxy que delega chamadas a uma instância real e permite reinstanciar esse delegate em runtime.

## Objetivos
- Demonstrar o padrão Proxy aplicado a beans Spring.
- Mostrar diferenças entre instanciar com `new` (fora do container) e obter instâncias gerenciadas pelo Spring.
- Expor endpoints para inspecionar e trocar o delegate em execução.

## Estrutura do projeto
- `src/main/java/br/com/designpattern/proxy/ProxyApplication.java` — Classe principal Spring Boot.
- `src/main/java/br/com/designpattern/proxy/service/ConfigurationDummyService.java` — Serviço "real" que possui um `serviceId` (UUID) gerado no construtor, métodos `logDelegateId()` e `simulateBehaviour()`.
- `src/main/java/br/com/designpattern/proxy/config/ConfigurationProxy.java` — Proxy que estende `ConfigurationDummyService`, contém um `AtomicReference<ConfigurationDummyService>` e métodos para delegar chamadas e `updateDelegate()` para trocar a instância.
- `src/main/java/br/com/designpattern/proxy/controller/DelegateController.java` — Endpoints REST para demonstrar comportamento do proxy e do serviço.
- `pom.xml` — configuração Maven (Java 21, Spring Boot 4.x).

## Como construir e executar
Requisitos:
- Java 21
- Maven 3.8+

Build:

mvn clean package

Executar (modo desenvolvimento):

mvn spring-boot:run

Ou executar o jar:

java -jar target/proxy-0.0.1-SNAPSHOT.jar

A aplicação roda por padrão em `http://localhost:8080`.

## Endpoints disponíveis
- `GET /api/v1/delegates/update-delegate`
  - Ação: chama `ConfigurationProxy.updateDelegate()` — cria uma nova instância de `ConfigurationDummyService` (via `new`) e a substitui no `AtomicReference` interno.
  - Retorno: 200 OK

- `GET /api/v1/delegates/current-delegate`
  - Retorna: `serviceId` do delegate atualmente referenciado pelo proxy.

- `GET /api/v1/delegates/amount`
  - Ações: chama `service.logDelegateId()` (o bean injetado no controller), coleta beans do `ApplicationContext` dos tipos `ConfigurationProxy` e `ConfigurationDummyService` e retorna um JSON contendo:
    - `proxyDelegateId` — id do delegate dentro do proxy
    - `beansConfigurationDummyService` — mapa de beans do tipo `ConfigurationDummyService` no contexto
    - `beansConfigurationProxy` — mapa de beans do tipo `ConfigurationProxy` no contexto

- `GET /api/v1/delegates/simulate`
  - Retorna: resultado de `service.simulateBehaviour()` (string contendo o `serviceId` do bean injetado no controller).

### Exemplos curl
- Atualizar delegate:

curl -i http://localhost:8080/api/v1/delegates/update-delegate

- Obter id atual do delegate dentro do proxy:

curl http://localhost:8080/api/v1/delegates/current-delegate

- Consultar beans e ids:

curl http://localhost:8080/api/v1/delegates/amount

- Simular comportamento do serviço injetado no controller:

curl http://localhost:8080/api/v1/delegates/simulate

## Análise de código (detalhada)

### ConfigurationDummyService
- Anotações: `@Service("configurationDummyService")`, `@Slf4j`, `@Getter` (Lombok).
- Comportamento: gera um UUID no construtor e o expõe como `serviceId`. Métodos:
  - `logDelegateId()` — loga o `serviceId` atual.
  - `simulateBehaviour()` — loga e retorna uma mensagem com o `serviceId`.
- Observação: por padrão é um bean singleton gerenciado pelo Spring.

### ConfigurationProxy
- Anotações: `@Component`, `@Primary`, `@Slf4j`.
- Implementação:
  - Extende `ConfigurationDummyService` para ser do mesmo tipo público.
  - Possui `private final AtomicReference<ConfigurationDummyService> delegate`.
  - Construtor recebe um `ConfigurationDummyService` (o bean original gerenciado) — Spring injeta aqui.
  - Métodos sobrescritos delegam para `delegate.get()`.
  - `updateDelegate()` cria `new ConfigurationDummyService()` e faz `delegate.set(newDelegate)`.
  - `currentDelegateId()` retorna `delegate.get().getServiceId()`.
- Implicações:
  - A instância criada por `new` não é gerenciada pelo Spring (sem injeções, AOP, proxies). Isso é aceitável para PoC, mas não ideal em produção.
  - `@Primary` faz com que o `ConfigurationProxy` seja preferido em autowired por tipo.
  - Usa `AtomicReference` e `synchronized` para segurança básica de concorrência.

### DelegateController
- Injeções: `ConfigurationProxy proxy`, `ApplicationContext applicationContext`, `ConfigurationDummyService service`.
- Observações importantes:
  - `service` (tipo `ConfigurationDummyService`) pode receber o `ConfigurationProxy` por causa de `@Primary`, dependendo de como o Spring resolve os beans por tipo/nome.
  - O endpoint `/amount` demonstra qual bean está registrado no contexto e qual foi injetado no controller.

## Observações sobre ciclo de vida e escopos
- O proxy é um bean gerenciado pelo Spring. O delegate inicial passado ao proxy no construtor é o bean `ConfigurationDummyService` gerenciado.
- Quando `updateDelegate()` usa `new ConfigurationDummyService()`, a nova instância foge ao gerenciamento do container. Ela não terá AOP, não participará de injeções e não receberá lifecycle callbacks.
- Abordagens melhores para obter instâncias gerenciadas dinamicamente:
  - Marcar `ConfigurationDummyService` como `@Scope("prototype")` e usar `ObjectProvider<ConfigurationDummyService>` ou `ObjectFactory`/`Provider` para criar instâncias gerenciadas on-demand.
  - Ou usar `applicationContext.getBean(ConfigurationDummyService.class)` se for prototype.

## Melhorias sugeridas
- Trocar a criação por `new` por `ObjectProvider` ou um `@Bean` factory para obter instâncias gerenciadas.
- Adicionar testes unitários e testes de integração para verificar swap de delegate e concorrência.
- Implementar uma estratégia de fallback/rollback (manter um histórico de delegates ou política de retry).
- Considerar o uso de proxies/decorações do Spring (AOP) para preservar comportamentos transversais.

## Logs e comportamento esperado
- Ao iniciar, haverá log do `ConfigurationDummyService` inicial e do `ConfigurationProxy` informando o delegate inicial.
- Chamadas a `/update-delegate` logam a atualização e o novo `serviceId` do delegate.

## Limitações desta PoC
- A nova instância é criada fora do contexto Spring — adequada apenas para demonstração.
- Segurança e políticas de concorrência são minimamente tratadas; para produção revisar locking e visibilidade entre threads.

## Licença
Exemplo educacional — use e adapte conforme necessário.

---

Se quiser que o README inclua também:
- um prompt em inglês/português para o gpt-5 mini, ou
- modifique `updateDelegate()` para usar `ObjectProvider` e atualize o código + testes,
posso aplicar as mudanças no repositório. Basta pedir.
