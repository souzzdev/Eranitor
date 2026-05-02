# 🤝 Guia de Contribuição — ERANITOR

Primeiro, **obrigado** por considerar contribuir para ERANITOR! Este documento fornece orientações para ajudar você a colaborar efetivamente.

---

## 📋 Índice

1. [Código de Conduta](#-código-de-conduta)
2. [Como Começar](#-como-começar)
3. [Tipos de Contribuição](#-tipos-de-contribuição)
4. [Fluxo de Desenvolvimento](#-fluxo-de-desenvolvimento)
5. [Padrões de Código](#-padrões-de-código)
6. [Commits e Pull Requests](#-commits-e-pull-requests)
7. [Testes](#-testes)
8. [Documentação](#-documentação)

---

## 💬 Código de Conduta

Todos os colaboradores devem seguir nosso [Código de Conduta](CODE_OF_CONDUCT.md).

**Resumo**:
- ✅ Seja respeitoso
- ✅ Abra discussões construtivas
- ✅ Aceite críticas positivamente
- ❌ Sem assédio, discriminação ou linguagem ofensiva

---

## 🚀 Como Começar

### Pré-requisitos

- **Java 21+** (verifique: `java -version`)
- **Maven 3.9+** (verifique: `mvn -version`)
- **PostgreSQL 14+** (verifique: `psql --version`)
- **Git** (verifique: `git --version`)
- **Node.js 18+** (opcional, para frontend)

### Setup Inicial

1. **Fork o repositório**
   ```bash
   # Clique em "Fork" no GitHub
   ```

2. **Clone seu fork**
   ```bash
   git clone https://github.com/seu-usuario/eranitor.git
   cd eranitor
   ```

3. **Adicione upstream remoto**
   ```bash
   git remote add upstream https://github.com/original-owner/eranitor.git
   ```

4. **Setup do banco de dados**
   ```bash
   # Crie database local
   createdb eranitor
   
   # Ou via psql
   psql -U postgres -c "CREATE DATABASE eranitor;"
   ```

5. **Configure variáveis de ambiente**
   ```bash
   # Copie o template
   cp src/main/resources/application-dev.properties.example \
      src/main/resources/application-dev.properties
   
   # Edite com seus valores locais
   # Spring detectará o profile 'dev' automaticamente
   ```

6. **Compile o projeto**
   ```bash
   mvn clean install
   ```

7. **Inicie o servidor**
   ```bash
   mvn spring-boot:run
   ```

   Backend estará em `http://localhost:8080`

### Verificação Rápida

```bash
# Teste o endpoint de health (não precisa autenticação)
curl http://localhost:8080/actuator/health

# Esperado:
# {"status":"UP"}
```

---

## 🎯 Tipos de Contribuição

### 🐛 Bug Fix
**Use quando**: Você encontrou algo quebrado

**Processo**:
1. Abra uma Issue descrevendo o bug
2. Crie uma branch: `git checkout -b fix/descricao-do-bug`
3. Corrija e teste
4. Abra PR com título: `fix: descrição curta`

**Exemplo**:
```
Título: fix: tarefa não marca como completa com status 500

Descrição:
- Ao clicar em checkbox, API retorna erro 500
- Verbo HTTP está incorreto (PATCH deveria ser POST)
- Corrigido em TarefaController.marcarConcluida()
```

---

### ✨ Feature Nova
**Use quando**: Você quer adicionar funcionalidade

**Processo**:
1. Discuta em uma Issue primeiro (evita work duplicate)
2. Branch: `git checkout -b feature/nome-da-feature`
3. Implemente incrementalmente
4. PR com título: `feat: descrição clara`

**Exemplo**:
```
Título: feat: adicionar filtro de tarefas por matéria

Descrição:
- Novo endpoint: GET /tarefas?materia_id=1
- Query param com validação
- Testes unitários inclusos
- Documentação Swagger atualizada
```

---

### 📚 Documentação
**Use quando**: Melhorar docs (README, Javadoc, comentários)

**Processo**:
1. Branch: `git checkout -b docs/melhoria`
2. Edite arquivos `.md` ou adicione Javadoc
3. PR com título: `docs: descrição`

---

### 🔧 Refactor/Cleanup
**Use quando**: Melhorar código sem mudar comportamento

**Processo**:
1. Branch: `git checkout -b refactor/componente`
2. Mantenha funcionalidade igual
3. Inclua testes
4. PR com título: `refactor: descrição`

---

### 🧪 Testes
**Use quando**: Adicionar cobertura de testes

**Processo**:
1. Branch: `git checkout -b test/feature-ou-componente`
2. Escreva testes unitários + integração
3. PR com título: `test: adicionar testes para X`

---

## 🔄 Fluxo de Desenvolvimento

### Git Workflow (Feature Branch)

```
main (produção)
  ↓
  ├─ develop (integração)
  │   ├─ feature/login
  │   ├─ feature/dashboard
  │   ├─ fix/token-refresh
  │   └─ docs/api
  │
  └─ hotfix/security-patch (apenas em produção)
```

### Passos para Contribuição

1. **Sincronize com upstream**
   ```bash
   git fetch upstream
   git rebase upstream/develop
   ```

2. **Crie sua branch**
   ```bash
   git checkout -b feature/minha-feature
   ```

3. **Desenvolva incrementalmente**
   ```bash
   git add .
   git commit -m "feat: mensagem clara"
   ```

4. **Push para seu fork**
   ```bash
   git push origin feature/minha-feature
   ```

5. **Abra Pull Request**
   - Descreva o que você fez
   - Reference issues relacionadas (`Closes #123`)
   - Aguarde review

6. **Responda a reviews**
   - Faça ajustes se solicitado
   - Não force-push (mantem histórico)

7. **Merge automático**
   - Após aprovação, será merged em `develop`
   - Automatic deploy será acionado

---

## 📝 Padrões de Código

### Java

#### Formatação
Usamos **Google Java Format**:

```bash
# Install (opcional)
wget https://github.com/google/google-java-format/releases/download/v1.17.0/google-java-format-1.17.0-all-deps.jar

# Ou use IDE plugin
# IntelliJ: Settings → Code Style → Scheme → Import → Google Style
# VS Code: Ctrl+Shift+P → Format Document
```

#### Estrutura de Classe

```java
package com.eranitor.tcc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pela lógica de tarefas.
 * 
 * <p>Responsabilidades:
 * - CRUD de tarefas
 * - Cálculo de prioridade
 * - Atualização de status
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TarefaService {
  
  private final TarefaRepository tarefaRepository;
  private final UsuarioRepository usuarioRepository;
  
  /**
   * Cria uma nova tarefa para o usuário.
   * 
   * @param usuarioId ID do usuário
   * @param request dados da tarefa
   * @return tarefa criada
   * @throws UsuarioNaoEncontradoException se usuário não existe
   */
  public TarefaDto criar(Integer usuarioId, CriarTarefaRequest request) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new UsuarioNaoEncontradoException());
    
    Tarefa tarefa = new Tarefa();
    tarefa.setUsuario(usuario);
    tarefa.setTitulo(request.getTitulo());
    // ... mapper completo
    
    Tarefa salva = tarefaRepository.save(tarefa);
    return mapToDto(salva);
  }
  
  private TarefaDto mapToDto(Tarefa tarefa) {
    // Implementar mapeamento
    return null;
  }
}
```

#### Naming Conventions

| O quê | Padrão | Exemplo |
|-------|--------|---------|
| Classe | PascalCase | `TarefaService` |
| Método | camelCase | `criarTarefa()` |
| Constante | UPPER_SNAKE_CASE | `MAX_TAREFAS_DIA` |
| Package | lowercase.dot.separated | `com.eranitor.tcc.service` |
| Variable | camelCase | `usuarioId` |
| Boolean | `is/has/can` | `isAtivo`, `hasPermissao` |

#### Anotações

```java
// ✅ Use Lombok para reduzir boilerplate
@Getter
@Setter
@RequiredArgsConstructor
public class Usuario {
  // Getters/setters automáticos
}

// ✅ Use Spring annotations
@Entity
@Service
@Repository
@Controller

// ❌ Evite
// Getters/setters manualmente
// Classes com muitos construtores
```

---

### Imports

```java
// ✅ Correto: Específico, curto
import com.eranitor.tcc.entity.Usuario;
import java.time.LocalDateTime;

// ❌ Evite: Wildcard
import com.eranitor.tcc.entity.*;
import java.time.*;
```

---

### Comments

```java
// ✅ Bom: Explica POR QUE
int prioridade = 10; // Peso do atraso (5) + proximidade (3) + dificuldade (2)

// ✅ Bom: Javadoc para métodos públicos
/**
 * Calcula a prioridade de uma tarefa.
 * 
 * @param tarefa a tarefa a priorizar
 * @return valor de prioridade (0-100)
 */
public int calcularPrioridade(Tarefa tarefa) { }

// ❌ Evite: Óbvio
int x = 5; // Incrementa x

// ❌ Evite: Commented code
// int y = 10;
// y = y + 5;
```

---

## 📌 Commits e Pull Requests

### Mensagens de Commit (Conventional Commits)

Siga padrão: `<type>(<scope>): <subject>`

```bash
# Format
<type>(<scope>): <subject>
<blank line>
<body>
<blank line>
<footer>

# Types
feat:     Nova feature
fix:      Correção de bug
docs:     Documentação
style:    Formatação (sem change semântico)
refactor: Refatoração (sem change de behavior)
perf:     Performance
test:     Testes
ci:       CI/CD config
chore:    Dependências, versioning

# Exemplos
feat(tarefa): adicionar filtro por matéria
fix(auth): refresh token não renovava corretamente
docs(readme): adicionar seção de deploy
test(prioridade): cobertura para algoritmo
```

### Pull Request Template

```markdown
## 📝 Descrição
Descrição clara do que foi alterado e por quê.

## 🔗 Issues Relacionadas
Closes #123, Relates #456

## 📋 Checklist
- [ ] Testes adicionados/atualizados
- [ ] Documentação atualizada
- [ ] Sem warnings ou erros no build
- [ ] Código segue padrões do projeto
- [ ] Commits seguem Conventional Commits

## 🧪 Como Testar
Passos para reproduzir ou testar a mudança:
1. Setup...
2. Execute...
3. Verifique...

## 📸 Screenshots (se aplicável)
[Adicione screenshots de UI changes]

## ⚠️ Breaking Changes
[Descreva se há breaking changes]
```

---

## 🧪 Testes

### Estrutura de Testes

```
src/test/java/
├── com/eranitor/tcc/service/
│   └── TarefaServiceTest.java
├── com/eranitor/tcc/controller/
│   └── TarefaControllerTest.java
└── com/eranitor/tcc/integration/
    └── TarefaIntegrationTest.java
```

### Exemplo de Teste Unitário

```java
@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {
  
  @Mock
  TarefaRepository tarefaRepository;
  
  @InjectMocks
  TarefaService tarefaService;
  
  @Test
  @DisplayName("Deve criar tarefa com sucesso")
  void deveCriarTarefaComSucesso() {
    // Arrange
    CriarTarefaRequest request = new CriarTarefaRequest();
    request.setTitulo("Estudar algoritmos");
    
    Tarefa tarefaSalva = new Tarefa();
    tarefaSalva.setId(1);
    tarefaSalva.setTitulo("Estudar algoritmos");
    
    when(tarefaRepository.save(any(Tarefa.class)))
        .thenReturn(tarefaSalva);
    
    // Act
    TarefaDto resultado = tarefaService.criar(1, request);
    
    // Assert
    assertNotNull(resultado);
    assertEquals("Estudar algoritmos", resultado.getTitulo());
    verify(tarefaRepository).save(any(Tarefa.class));
  }
}
```

### Exemplo de Teste de Integração

```java
@SpringBootTest
@AutoConfigureMockMvc
class TarefaIntegrationTest {
  
  @Autowired
  MockMvc mockMvc;
  
  @Autowired
  TarefaRepository tarefaRepository;
  
  @Test
  @WithMockUser(username = "user@example.com")
  void deveListarTarefasDoUsuario() throws Exception {
    // Setup
    tarefaRepository.deleteAll();
    // ... criar dados de teste
    
    // Execute & Assert
    mockMvc.perform(get("/api/v1/tarefas")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray());
  }
}
```

### Rodando Testes

```bash
# Todos os testes
mvn test

# Teste específico
mvn test -Dtest=TarefaServiceTest

# Com cobertura
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html

# Teste de integração
mvn verify
```

### Mínimos Esperados

- ✅ Cobertura > 60% (MVP)
- ✅ Cobertura > 80% (Pré-produção)
- ✅ Sem testes `@Ignore` sem motivo documentado
- ✅ Testes devem ser independentes (sem ordem específica)

---

## 📚 Documentação

### Javadoc

```java
/**
 * Calcula o score de prioridade de uma tarefa.
 * 
 * <p>O cálculo considera:
 * <ul>
 *   <li>Dificuldade: 0-5 (peso 2)</li>
 *   <li>Dias até deadline: (peso 3)</li>
 *   <li>Dias de atraso: (peso 5)</li>
 *   <li>Progresso: 0-100% (peso -2)</li>
 * </ul>
 * 
 * <p>Exemplo:
 * <pre>
 *   Tarefa tarefa = new Tarefa();
 *   tarefa.setDificuldade(4);
 *   int prioridade = calcularPrioridade(tarefa); // Resultado: ~25
 * </pre>
 *
 * @param tarefa a tarefa para calcular prioridade
 * @return score de prioridade (0-100)
 * @throws NullPointerException se tarefa é null
 * @see Tarefa
 */
public int calcularPrioridade(Tarefa tarefa) { }
```

### README de Módulo

Crie `README.md` em diretórios principais:

```markdown
# Módulo de Tarefas

Responsável pelo CRUD e lógica de tarefas.

## Estrutura

- `entity/Tarefa.java` - Modelo JPA
- `repository/TarefaRepository.java` - Data access
- `service/TarefaService.java` - Lógica de negócio
- `controller/TarefaController.java` - REST endpoints

## APIs

- `GET /api/v1/tarefas` - Listar tarefas do usuário
- `POST /api/v1/tarefas` - Criar tarefa
- `GET /api/v1/tarefas/{id}` - Obter detalhes
- `PUT /api/v1/tarefas/{id}` - Atualizar
- `DELETE /api/v1/tarefas/{id}` - Deletar

## Testes

```bash
mvn test -Dtest=TarefaServiceTest
```

## Melhorias Futuras

- [ ] Suporte a subtarefas
- [ ] Histórico de mudanças
- [ ] Comentários em tarefas
```

---

## 🔍 Review Checklist

Antes de submeter PR, verifique:

- [ ] **Código**
  - [ ] Segue padrões do projeto
  - [ ] Sem duplicação
  - [ ] Legível e bem comentado
  - [ ] Sem TODOs sem issue

- [ ] **Testes**
  - [ ] Novos testes adicionados
  - [ ] Todos passam (`mvn test`)
  - [ ] Cobertura não diminuiu

- [ ] **Documentação**
  - [ ] Javadoc atualizado
  - [ ] README/guias relevantes atualizados
  - [ ] Comentários explicam lógica complexa

- [ ] **Commits**
  - [ ] Mensagens claras
  - [ ] Histórico lógico (sem "fix typo" 10x)
  - [ ] Nenhum debug/console.log

- [ ] **Git**
  - [ ] Sem conflitos com `develop`
  - [ ] Sem arquivos desnecessários
  - [ ] Branch atualizada com upstream

---

## 📞 Precisa de Ajuda?

- **Dúvida técnica**: Abra uma Discussion no GitHub
- **Encontrou bug**: Abra uma Issue com `[BUG]`
- **Quer sugerir feature**: Abra uma Issue com `[FEATURE]`
- **Quer discutir design**: Abra uma Discussion com `[RFC]`

---

## 🎉 Dicas para Boas Contribuições

1. **Comece pequeno**: Seu primeiro PR não precisa ser épico
2. **Comunique**: Se tiver dúvida, pergunta não custa
3. **Revise seu próprio código primeiro**: Detecta 70% dos problemas
4. **Teste localmente**: Não submeta sem testar
5. **Seja respeitoso**: Toda contribuição é valiosa
6. **Aprenda com feedback**: Use reviews como educação

---

## 📖 Recursos

- [Git - Official Documentation](https://git-scm.com/doc)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Best Practices](https://spring.io/guides/gs/securing-web/)

---

<div align="center">

**Obrigado por contribuir para tornar ERANITOR melhor! 🚀**

[⬆ Voltar ao topo](#-guia-de-contribuição--eranitor)

</div>
