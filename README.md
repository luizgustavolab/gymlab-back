# 🏋️‍♂️🖥️ GymLab API — Backend Engine

GymLab API é o núcleo inteligente do ecossistema GymLab, responsável por toda a lógica de negócio relacionada à geração de treinos personalizados, validação de regras de exercícios e persistência de dados dos usuários.

A API foi construída com Spring Boot + JPA + PostgreSQL (Supabase) e implementa um motor de geração de treino baseado em Strategy Pattern + Template System, permitindo a criação dinâmica de fichas de treino baseadas em objetivos como hipertrofia, força e emagrecimento.

---

# 🧠 Arquitetura Geral

O backend segue uma arquitetura em camadas baseada em responsabilidades bem definidas:

Frontend (Angular)
        ↓
Controller Layer (REST API)
        ↓
Service / Engine Layer (WorkoutEngine)
        ↓
Strategy Layer (Objetivos de Treino)
        ↓
Template Layer (Estrutura Semanal)
        ↓
Rules Engine (Categorias de Exercício)
        ↓
Repository Layer (JPA)
        ↓
PostgreSQL (Supabase)

---


# ⚙️ Tecnologias Utilizadas

Tecnologia | Função
| :--- | :--- |
| Java 21 | Linguagem principal do backend |
| Spring Boot 3+ | Framework base da API REST |
| Spring Web | Controle de endpoints HTTP |
| Spring Security (JWT) | Autenticação via Supabase |
| Spring Data JPA | Persistência e ORM |
| Hibernate | Mapeamento objeto-relacional |
| PostgreSQL (Supabase) | Banco de dados relacional |
| Jackson | Serialização JSON |
| Maven | Gerenciamento de dependências |

---

# 🔐 Autenticação e Segurança

O sistema utiliza Supabase Auth (JWT stateless).

- Fluxo de autenticação:
1. Frontend autentica via Supabase
2. JWT é gerado pelo provider
3. Token é enviado via header:
      Authorization: Bearer <jwt>
4. Backend valida o token automaticamente via Spring Security
5. O userId é extraído do sub do JWT:
    UUID userId = UUID.fromString(jwt.getSubject());

# 🧩 Módulo de Geração de Treino

> Endpoint principal
       /api/treinos/gerar
- Responsabilidade
    Gera uma ficha completa de treino baseada em:
      Objetivo do usuário
      Gênero
      Dias de treino por semana
      Base de exercícios cadastrados

> Fluxo interno
- Validação do JWT
- Remoção de treinos antigos do usuário
- Consulta de exercícios no banco
- Construção do WorkoutContext
- Execução do motor de estratégia
- Persistência dos treinos gerados
- Retorno de DTO para o frontend

---

# 🧠 Motor de Estratégias (Strategy Pattern)

O sistema utiliza Strategy Pattern para separar regras de geração por objetivo.

- Estrutura:
    HipertrofiaStrategy
    ForcaStrategy
    EmagrecimentoStrategy
    CondicionamentoStrategy (ou derivado)

- Regra de seleção
    boolean supports(String objetivo)
Cada estratégia responde se é compatível com o objetivo solicitado.

- Prioridade
Cada strategy possui prioridade:
    int priority()
Isso permite ordenação de execução quando necessário.

- Problema crítico resolvido
Bug encontrado:
.sorted(Comparator.comparingDouble(e -> Math.random()))
Problema:
viola contrato do Comparator
causava crash no TimSort
✔ Solução:
Collections.shuffle(lista)

---

# 🧱 Template System
- Responsabilidade
  Define a estrutura semanal do treino:
    divisão de dias
    grupos musculares
    quantidade de exercícios
    séries e repetições

- TemplateResolver
Responsável por delegar o template correto:
    TreinoTemplate resolve(ObjetivoTreino objetivo, Genero genero, int dias)

- Problema identificado
  Apenas HipertrofiaTemplateProvider existia inicialmente
  Mas sistema suporta múltiplos objetivos
Solução arquitetural:
Refatoração para múltiplos providers:
HipertrofiaTemplateProvider
ForcaTemplateProvider
EmagrecimentoTemplateProvider
E resolução via switch por objetivo.

---

# 🧩 Regras de Categoria de Exercício
- CategoriaTreinoRules
Responsável por validar se um exercício pode ser usado em um objetivo específico.
- Objetivos suportados:
HIPERTROFIA → Musculação, Powerlifting
FORÇA → Powerlifting, Strongman etc
EMAGRECIMENTO → Condicionamento, Calistenia
DEFINIÇÃO → híbrido leve
- Validação
    categoriaValida(ObjetivoTreino objetivo, String categoria)
- Problema identificado
comparação sensível a formato de string
risco de inconsistência de dados
- Mitigação
normalização de strings
uso de Set por objetivo

---

# 🏋️ Sistema de Prioridade de Exercícios
- ExercisePriorityCatalog
Define exercícios prioritários por grupo muscular:
Exemplo:
Peito:
bench press
incline bench press
dumbbell press
Costas:
pull up
lat pulldown
barbell row
Quadríceps:
squat
leg press
lunge
- Objetivo
Garantir que exercícios compostos e mais eficientes tenham prioridade no algoritmo.

---

# 🗄️ Persistência (Supabase / PostgreSQL)
- Entidade principal
TreinoUsuario
  Armazena:
    userId
    exercício
  grupo muscular
  séries / repetições
  objetivo
  data de criação
  metadata do exercício
  Operações principais
  deleteByUserId (reset de ficha)
  saveAll (persistência em lote)
  findByUserId (consulta de dashboard)
🚨 Problemas críticos já resolvidos
1. Crash de Comparator (500 error)
✔ Corrigido com shuffle seguro
2. Explosão de inserts
✔ Normalizado (~35 registros)
3. Inconsistência de template
✔ Identificado e estruturado para múltiplos providers

---

# 🔄 Fluxo final do sistema

Angular Frontend
      ↓
POST /api/treinos/gerar
      ↓
TreinoController
      ↓
JWT Validation (Supabase)
      ↓
WorkoutContext
      ↓
WorkoutEngine
      ↓
Strategy (por objetivo)
      ↓
TemplateResolver
      ↓
Rules Engine (categorias)
      ↓
JPA Repository
      ↓
PostgreSQL (Supabase)

---

# 📦 Estrutura de Pacotes
com.gymlab.api
 ├── controller (TreinoController)
 ├── engine (TreinoEngine)
 ├── strategy (Hipertrofia, Forca, etc)
 ├── template (TemplateResolver + Providers)
 ├── rules (CategoriaTreinoRules)
 ├── model (TreinoUsuario, Exercicio)
 ├── dto (TreinoDashboardDto)
 ├── repository (JPA Repositories)
 └── security (JWT integration)

 ---

# 🚀 Como Executar o Backend Localmente

> Clone o repositório
git clone https://github.com/luizgustavolab/gymlab-back.git
cd gymlab-back

> Configurar variáveis de ambiente
Criar:
    src/main/resources/application.properties
Exemplo:
    spring.datasource.url=jdbc:postgresql://<supabase-url>
    spring.datasource.username=postgres
    spring.datasource.password=<senha>
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    server.port=8080

> Rodar a aplicação
    ./mvnw spring-boot:run

> Endpoint principal
http://localhost:8080/api/treinos/gerar
http://localhost:8080/api/exercicios

---

# 📌 Status do projeto
- ✔ Concluído
API REST funcional
Motor de geração de treino
Strategy Pattern implementado
Integração com Supabase Auth
Persistência no PostgreSQL
Regras de categoria
DTO de dashboard
- ⚠ Em evolução
Template system multi-objetivo completo
Refinamento do engine de seleção de exercícios
Melhorias de performance e deduplicação avançada

---

# 📄 Licença

Projeto privado para fins de estudo, engenharia de software aplicada, e experimentação de sistemas inteligentes de prescrição de treino.