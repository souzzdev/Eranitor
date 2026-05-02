# 📚 ERANITOR — Sistema Inteligente de Gestão de Estudos

> **Transformando dados de estudo em decisões inteligentes**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-green?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-336791?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![Angular](https://img.shields.io/badge/Angular-Latest-DD0031?style=for-the-badge&logo=angular)](https://angular.io/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

---

## 🎯 Visão Geral

**ERANITOR** é uma plataforma educacional inteligente que vai além de um simples gerenciador de tarefas. É um **motor de decisão para otimização de estudo** — projetado para responder automaticamente a pergunta que todo estudante faz:

> **"O que devo estudar hoje?"**

Diferente de ferramentas tradicionais, ERANITOR implementa um **algoritmo de priorização dinâmica** que considera urgência, dificuldade, atraso e progresso para sugerir o caminho de estudo mais eficiente.

---

## 🧠 Diferencial — A Inteligência por Trás

### 🔬 Algoritmo de Priorização Adaptativo

O sistema não apenas lista tarefas — ele as **ordena inteligentemente** usando múltiplos sinais:

```
Prioridade = (Dificuldade × 2) + (Proximidade Data × 3) + (Atraso × 5) - (Progresso × 2)
```

| Fator | Peso | Lógica |
|-------|------|--------|
| **Atraso** | 5 | Tarefas vencidas recebem peso máximo |
| **Proximidade da Data** | 3 | Urgência aumenta conforme deadline aproxima |
| **Dificuldade** | 2 | Matérias complicadas exigem mais tempo |
| **Progresso** | -2 | Reduz prioridade conforme você avança |

### ✨ Resultados Práticos

- **Dashboard inteligente** sugere "Top 3 tarefas do dia"
- **Distribuição automática** de horas por matéria (baseada em peso vestibular + dificuldade)
- **Alertas contextualizados** para tarefas atrasadas
- **Metrificação contínua** de progresso semanal

---

## 🏗️ Arquitetura do Sistema

### Stack Tecnológico

```
┌─────────────────────────────────────────────────────────┐
│                   Frontend (SPA)                        │
│  Angular + TypeScript + RxJS (Vercel)                   │
└────────────────────┬────────────────────────────────────┘
                     │ REST API (HTTP/S)
┌────────────────────▼────────────────────────────────────┐
│              Backend (Spring Boot)                      │
│  Java 21 + Spring Security + Spring Data JPA (Render)   │
└────────────────────┬────────────────────────────────────┘
                     │ JDBC
┌────────────────────▼────────────────────────────────────┐
│            Database (PostgreSQL)                        │
│  Transações ACID + Integridade Referencial              │
└─────────────────────────────────────────────────────────┘
```

### Camadas Backend (MVC + Service)

```java
Controller → Service → Repository → Entity (JPA)
```

- **Controller**: Endpoints REST com validação
- **Service**: Lógica de negócio (algoritmo de priorização, cálculos)
- **Repository**: Acesso a dados via Spring Data JPA
- **Entity**: Modelos persistidos (Usuario, Materia, Tarefa, etc.)

---

## 📋 Funcionalidades Principais

### 🔐 Autenticação e Segurança
- [x] Login/Registro com JWT + Refresh Token
- [x] Validação de senha forte (8+ chars, maiúscula, minúscula, número, símbolo)
- [x] Hash bcrypt para senhas
- [x] CORS configurado
- [x] Atualização automática de token

### 📚 Gestão de Estudos
- [x] **Matérias**: Criar, editar, deletar com peso vestibular e dificuldade
- [x] **Tópicos**: Organizar conteúdo dentro de matérias
- [x] **Tarefas**: Criar com descrição, data limite, dificuldade (1-5)
- [x] **Tempo de Estudo**: Registro manual por tipo (exercício, vídeo, apostila)
- [x] **Meta Semanal**: Definir horas alvo e receber distribuição automática

### 📊 Analytics e Dashboard
- [x] Gráfico de desempenho por matéria (pizza)
- [x] Gráfico de tipo de estudo (barras empilhadas)
- [x] Progresso geral (%)
- [x] Horas estudadas vs meta
- [x] Listagem de tarefas com código de cores

### 📅 Agenda Inteligente
- [x] Calendário mensal interativo
- [x] Múltiplos tipos de evento (Prova, Atividade, Apresentação, Trabalho)
- [x] Cores diferenciadas por tipo
- [x] Detalhes ao clicar no dia

### 🔔 Notificações
- [x] Push notification diária
- [x] In-app notifications
- [x] Mensagens motivacionais baseadas em desempenho
- [x] Alertas de tarefas próximas

---

## 🗄️ Modelo de Dados

### Entidades Principais

```
Usuario
├── id (PK)
├── nome
├── login (email único)
├── senhaHash (bcrypt)
├── serie
├── instituicao
├── criadoEm
└── 1:N → Materia, Tarefa, Agenda, Desempenho

Materia
├── id (PK)
├── nome
├── ativa (boolean)
├── usuario_id (FK)
├── peso_vestibular
├── dificuldade (baixo|médio|alto)
└── 1:N → Topico, Tarefa

Topico
├── id (PK)
├── nome
├── descricao
├── materia_id (FK)
└── 1:N → Tarefa

Tarefa
├── id (PK)
├── titulo
├── descricao
├── dataLimite
├── dificuldade (1-5)
├── status (pendente|concluída)
├── prioridade_calculada
├── usuario_id (FK)
├── topico_id (FK)
└── 1:N → TempoEstudo

TempoEstudo
├── id (PK)
├── tipo (exercicio|video|apostila)
├── duracao (minutos)
├── data
├── tarefa_id (FK)

Agenda
├── id (PK)
├── titulo
├── tipo (Prova|Atividade|Apresentação|Trabalho)
├── data
├── usuario_id (FK)

Desempenho
├── id (PK)
├── usuario_id (FK)
├── materia_id (FK)
├── mes, ano
├── horasConcluidas
├── horasMeta
```

### Relacionamentos
- **Usuário → Matérias**: 1:N
- **Usuário → Tarefas**: 1:N
- **Matéria → Tópicos**: 1:N
- **Matéria → Desempenho**: 1:N
- **Tópico → Tarefas**: 1:N

---

## 🚀 Começando

### Pré-requisitos

- **Java 21+**
- **Maven 3.9+**
- **PostgreSQL 14+**
- **Node.js 18+** (para frontend)
- **Angular CLI 17+**

### Setup Backend

1. **Clone o repositório**
   ```bash
   git clone https://github.com/seu-usuario/eranitor.git
   cd eranitor
   ```

2. **Configure o banco de dados**
   ```bash
   createdb eranitor
   # Ou use seu cliente PostgreSQL favorito
   ```

3. **Configure variáveis de ambiente** (`.env` ou `application.properties`)
   ```properties
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/eranitor
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=sua_senha
   JWT_SECRET=sua_chave_secreta_super_segura
   JWT_EXPIRATION=3600000
   JWT_REFRESH_EXPIRATION=604800000
   ```

4. **Execute migrations** (se usar Flyway/Liquibase)
   ```bash
   mvn flyway:migrate
   ```

5. **Compile e inicie o servidor**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Servidor estará em: `http://localhost:8080`

### Setup Frontend

1. **Navegue até o diretório frontend**
   ```bash
   cd frontend
   ```

2. **Instale dependências**
   ```bash
   npm install
   ```

3. **Configure API endpoint** (environment.ts)
   ```typescript
   export const environment = {
     apiUrl: 'http://localhost:8080/api'
   };
   ```

4. **Inicie o servidor de desenvolvimento**
   ```bash
   ng serve
   ```

   Aplicação estará em: `http://localhost:4200`

---

## 📡 API REST — Endpoints Principais

### Autenticação
```http
POST   /auth/register         # Criar nova conta
POST   /auth/login            # Fazer login
POST   /auth/refresh          # Renovar JWT
```

### Usuário
```http
GET    /usuarios/{id}         # Obter perfil
PUT    /usuarios/{id}         # Atualizar perfil
PUT    /usuarios/{id}/senha   # Alterar senha
```

### Matérias
```http
GET    /usuarios/{id}/materias         # Listar matérias
POST   /usuarios/{id}/materias         # Criar matéria
PUT    /usuarios/{id}/materias/{id}    # Editar matéria
DELETE /usuarios/{id}/materias/{id}    # Deletar matéria
```

### Tarefas
```http
GET    /usuarios/{id}/tarefas                    # Listar tarefas
GET    /usuarios/{id}/tarefas/priorizadas       # Tarefas ordenadas por prioridade
GET    /usuarios/{id}/tarefas/sugestao-diaria   # Top 3 tarefas do dia
POST   /usuarios/{id}/tarefas                    # Criar tarefa
PUT    /usuarios/{id}/tarefas/{id}               # Editar tarefa
DELETE /usuarios/{id}/tarefas/{id}               # Deletar tarefa
PATCH  /usuarios/{id}/tarefas/{id}/status       # Marcar como completa
```

### Desempenho
```http
GET    /usuarios/{id}/desempenho        # Gráficos de progresso
GET    /usuarios/{id}/desempenho/tempo  # Horas vs meta
```

### Agenda
```http
GET    /usuarios/{id}/agenda        # Eventos do calendário
POST   /usuarios/{id}/agenda        # Criar evento
PUT    /usuarios/{id}/agenda/{id}   # Editar evento
DELETE /usuarios/{id}/agenda/{id}   # Deletar evento
```

---

## ⚙️ Requisitos Não-Funcionais

| Requisito | Meta | Status |
|-----------|------|--------|
| **Performance** | < 3s de carregamento inicial | ✅ |
| **Responsividade** | Mobile-first (320px, 768px, 1024px+) | ✅ |
| **Uptime** | 99% disponibilidade | 🔄 (Em produção) |
| **Testes** | > 80% cobertura unitária | 🔄 |
| **Segurança** | WCAG 2.1 AA + HTTPS + CORS | ✅ |
| **Escalabilidade** | Milhares de usuários, milhões de tarefas | 🔄 |

---

## 🧪 Testes

### Executar testes unitários
```bash
mvn test
```

### Executar testes com cobertura
```bash
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

### Testes de integração
```bash
mvn verify
```

---

## 📈 Roadmap — Evoluções Futuras

### 🚀 Phase 2: IA Leve
- [ ] Ajuste automático de pesos da fórmula de priorização
- [ ] Previsão de procrastinação
- [ ] Recomendações baseadas em histórico

### 🎮 Phase 3: Gamificação
- [ ] Sistema de XP por tarefa concluída
- [ ] Níveis de estudo
- [ ] Streaks diários
- [ ] Ranking (público ou privado)

### 📊 Phase 4: Análise Avançada
- [ ] Exportação de relatórios (PDF/Excel)
- [ ] Análise de padrões de estudo
- [ ] Previsão de performance em provas
- [ ] Integração com calendários externos (Google Calendar, etc.)

### 🤖 Phase 5: ML/IA Avançada
- [ ] Recomendador baseado em ML
- [ ] Detecção de burn-out
- [ ] Sugestão de tempo ótimo para estudar
- [ ] Análise de estilo de aprendizado

---

## 🔒 Segurança

### Implementado
- ✅ JWT com refresh token
- ✅ Hash bcrypt para senhas
- ✅ CORS configurado
- ✅ Validação backend obrigatória
- ✅ HTTPS (em produção)
- ✅ HttpOnly cookies para tokens

### Recomendações para Produção
- [ ] Implementar rate limiting
- [ ] Usar Web Application Firewall (WAF)
- [ ] Auditing de operações críticas
- [ ] Backup automático do banco de dados
- [ ] Monitoramento de segurança contínuo

---

## 📦 Deploy

### Backend (Render)
1. Conectar repositório GitHub ao Render
2. Configurar variáveis de ambiente
3. Render detecta `pom.xml` e executa `mvn clean install`
4. Deploy automático em cada push para `main`

### Frontend (Vercel)
1. Conectar repositório GitHub ao Vercel
2. Configurar build command: `ng build --prod`
3. Output directory: `dist/`
4. Deploy automático em cada push para `main`

### Banco de Dados (PostgreSQL Cloud)
- Sugestão: **Supabase**, **Railway** ou **Render Managed Database**

---

## 🤝 Contribuindo

Aceitamos contribuições! Para colaborar:

1. Faça um **fork** do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um **Pull Request**

### Padrões de Código
- Java: Lombok para reduzir boilerplate
- Formatação: Google Java Format
- Testes: Obrigatório para features novas
- Commits: Seguir padrão [Conventional Commits](https://www.conventionalcommits.org/)

---

## 📝 Documentação

- **Especificação de Requisitos**: `docs/REQUISITOS.md`
- **Arquitetura**: `docs/ARQUITETURA.md`
- **Guia de Contribuição**: `CONTRIBUTING.md`
- **Javadoc**: Execute `mvn javadoc:javadoc`

---

## 📞 Suporte

### Encontrou um bug?
Abra uma **Issue** com:
- Título descritivo
- Reprodução do problema
- Comportamento esperado vs atual
- Prints/logs se aplicável

### Tem uma ideia?
Crie uma **Discussion** ou **Issue** com tag `enhancement`

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** — veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 🙏 Agradecimentos

- Todos os contribuidores que fizeram isso possível

---

## 📊 Status do Projeto

| Componente | Status | Versão |
|-----------|--------|--------|
| **Backend** | ✅ MVP | 0.1.0 |
| **Frontend** | 🔄 Em desenvolvimento | - |
| **Database** | ✅ Schema completo | - |
| **Deploy** | 🔄 Em setup | - |

---

## 📈 Métricas

```
Total de Requisitos: 41 (14 RN + 18 RF + 15 RNF)
Funcionalidades MVP: 28/41 implementadas (68%)
Cobertura de Testes: [Verificar com: mvn test jacoco:report]
Performance: ~2.5s (inicial), <500ms (APIs)
```

---

## 💡 Perguntas Frequentes

**P: Preciso de um frontend Angular?**
R: A especificação prevê Angular SPA, mas você pode usar React/Vue se preferir. A API é agnóstica.

**P: Posso usar MySQL em vez de PostgreSQL?**
R: Sim, mas será necessário ajustar o dialect do Hibernate em `application.properties`.

**P: Como funciona o algoritmo de priorização?**
R: Veja a [documentação técnica](docs/ALGORITMO.md) para detalhes da fórmula e exemplos.

**P: Preciso de Docker?**
R: Não é obrigatório, mas fornecemos `Dockerfile` na raiz do projeto.

---

<div align="center">

### ⭐ Se este projeto ajudou você, considere dar uma estrela!

[⬆ Voltar ao topo](#-eranitor--sistema-inteligente-de-gestão-de-estudos)

</div>
