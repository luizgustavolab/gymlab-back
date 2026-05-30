package com.gymlab.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    private final RestTemplate restTemplate;

    @Value("${ollama.url}")
    private String ollamaUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public AiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(120000);
        this.restTemplate = new RestTemplate(factory);
    }

    public String gerarFicha(String objetivo, Integer diasPorSemana, List<Exercicio> exercicios) {

        StringBuilder listaExercicios = new StringBuilder();

        for (Exercicio exercicio : exercicios) {
            listaExercicios
                .append("- externalId: ")
                .append(exercicio.getExternalId())
                .append(" | nome: ")
                .append(exercicio.getNome())
                .append(" | categoria: ")
                .append(exercicio.getCategoria())
                .append("\n");
        }

        String prompt = """
Você é um gerador de fichas de treino estruturadas.

REGRA CRÍTICA:
Você SÓ pode usar exercícios da lista fornecida.
Você NÃO pode inventar exercícios.
Você DEVE usar apenas externalId.

RETORNE SOMENTE JSON VÁLIDO.
NÃO use markdown.
NÃO explique.
NÃO escreva texto fora do JSON.

FORMATO OBRIGATÓRIO:
[
  {
    "diaSemana": "Segunda",
    "grupoMuscular": "Peito",
    "exercicioExternalId": "Barbell_Bench_Press",
    "series": 3,
    "repeticoes": 10,
    "intervalo": "60s"
  }
]

IMPORTANTE:
- use apenas externalId da lista
- nunca invente exercício
- respeite o objetivo e nível do usuário

Objetivo: %s
Dias por semana: %d

EXERCÍCIOS DISPONÍVEIS:
%s
""".formatted(objetivo, diasPorSemana, listaExercicios.toString());

        Map<String, Object> request = Map.of(
            "model", ollamaModel,
            "prompt", prompt,
            "stream", false
        );

        try {
            ResponseEntity<Map> response =
                restTemplate.postForEntity(ollamaUrl, request, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("response")) {
                throw new RuntimeException("Resposta do Ollama veio vazia ou inválida.");
            }

            return response.getBody().get("response").toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro na comunicação com o Ollama: " + e.getMessage());
        }
    }
}