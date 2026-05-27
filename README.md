# API GymLab 🏋️‍♂️🤖

GymLab é uma API projetada para orquestrar o gerenciamento de exercícios físicos e a geração inteligente de fichas de treino personalizadas. A plataforma permite o cadastro de perfil biométrico e utiliza inteligência artificial para montar rotinas baseadas em hipertrofia, força, definição ou emagrecimento.

## 🧠 Filosofia e Decisões de Arquitetura (O Laboratório)

Embora o projeto tenha nascido como um laboratório prático de estudos, ele é fundamentado em lógica de engenharia de software real e escalável. 

A principal decisão arquitetural foi **não utilizar o PostgREST nativo do Supabase** (que expõe o banco diretamente para o front-end), optando pela construção de um middleware em **Java com Spring Boot**. Os motivos incluem:

1. **Segurança e Regras de Negócio Ocultas:** A API Java atua como um escudo. O *Row Level Security* (RLS) do Supabase blinda o banco de acessos diretos via internet, enquanto o Spring Security (via OAuth2 Resource Server) intercepta os JWTs, valida chaves assimétricas (JWKS) e garante que apenas usuários autenticados gravem dados, sem expor a lógica de orquestração no client-side.
2. **Orquestração de Inteligência Artificial:** Fichas de treino não são um simples CRUD. A geração inteligente exige cálculo de IMC, análise de objetivos (cargas, repetições, intervalos) e integração com agentes LLM. Um backend dedicado é o único local seguro e performático para lidar com essa camada sem sobrecarregar o front-end ou expor chaves de API da IA.
3. **Contratos Estritos (DTOs e Validações):** A utilização do Jakarta Bean Validation garante a integridade estrutural e semântica antes mesmo de tocar no banco de dados.

## 🔧 Tecnologias Utilizadas

| Tecnologia | Uso |
| :--- | :--- |
| **Java 21** | Linguagem principal de desenvolvimento |
| **Spring Boot 3** | Framework base da aplicação |
| **Spring Security (OAuth2)** | Interceptação e validação de JWTs via JWKS |
| **PostgreSQL / Supabase** | Banco de dados relacional e provedor de Autenticação |
| **Jakarta Validation** | Validação sintática de payloads (DTOs) |
| **Maven** | Gerenciamento de dependências e build |

## 🚀 Como Executar o Projeto Localmente

1. Clone o repositório:
   ```bash
   git clone [https://github.com/luizgustavolab/gymlab-back.git](https://github.com/luizgustavolab/gymlab-back.git)

2. Configure as variáveis de ambiente no arquivo src/main/resources/application.properties:
    spring.datasource.url=jdbc:postgresql://[SEU_HOST_SUPABASE]:5432/postgres
    spring.datasource.username=postgres
    spring.datasource.password=[SUA_SENHA_DO_BANCO]
    spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://[ID_DO_PROJETO].supabase.co/rest/v1/auth/keys

3. Na raiz do projeto, execute o comando Maven para baixar as dependências e iniciar o servidor:
    .\mvnw.cmd spring-boot:run

A API estará disponível em http://localhost:8080.

## 💪📗 Documentação da API
Endpoints
S01 - Listagem de Exercícios Catálogo
GET /api/exercicios
Rota pública. Retorna o catálogo estático de exercícios disponíveis na base.

Response JSON Exemplo (200 OK):
[
  {
    "id": "e3beb492-51c4-4f9d-afd7-aed8ee1461c4",
    "nome": "Supino Reto",
    "grupoMuscular": "Peito",
    "maquina": false
  }
]

S02 - Inserção de Exercício Isolado na FichaPOST /api/treinosRota Privada. Requer Token JWT no header Authorization: Bearer <token>.

Parâmetro               Tipo                Descrição
exercicioId             UUID                Obrigatório.ID do exercício do catálogo.
diaSemana               String              Obrigatório. Ex: "Segunda-feira".
grupoMuscular           String              Obrigatório.
series                  Int                 Obrigatório. Mínimo 1.
repeticoes              Int                 Obrigatório. Mínimo 1.
intervalo               String              Opcional. Tempo de descanso.

Request JSON Exemplo:
{
  "exercicioId": "e3beb492-51c4-4f9d-afd7-aed8ee1461c4",
  "diaSemana": "Segunda-feira",
  "grupoMuscular": "Peito",
  "series": 4,
  "repeticoes": 12,
  "intervalo": "60s"
}

Response Status         Descrição
201                     Criado com sucesso.
400                     Dados estruturais inválidos (campos vazios ou negativos).
401                     Token ausente, inválido ou expirado.
404                     ID do exercício não encontrado no catálogo.

S03 - Geração de Ficha via IA (Orquestrador)
POST /api/treinos/gerar
Rota Privada. Requer Token JWT. Aciona o Agente Inteligente para orquestrar a ficha de treinos completa baseada na fisiologia do usuário.

Parâmetro	        Tipo	    Descrição
genero	            String	    Obrigatório. Masculino ou Feminino.
peso	            Double	    Obrigatório. Usado para cálculo de IMC. Mínimo 30kg.
altura	            Double	    Obrigatório. Usado para cálculo de IMC.
objetivo	        String	    Obrigatório. (Hipertrofia, Força, Definição, Emagrecimento).
diasPorSemana	    Int	        Obrigatório. Frequência de idas à academia (1 a 7).
feedbackAjuste	    String	    Opcional. Instruções extras para o agente de IA.

Request JSON Exemplo:
{
  "genero": "Masculino",
  "peso": 85.5,
  "altura": 1.80,
  "objetivo": "hipertrofia",
  "diasPorSemana": 5,
  "feedbackAjuste": "Focar desenvolvimento de pernas"
}

(Nota: Response body pendente de implementação do agente de IA).

