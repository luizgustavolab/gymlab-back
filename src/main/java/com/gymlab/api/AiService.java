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

        SimpleClientHttpRequestFactory factory =
            new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(5000);

        factory.setReadTimeout(120000);

        this.restTemplate =
            new RestTemplate(factory);
    }

    public String gerarFicha(
        String objetivo,
        Integer diasPorSemana,
        List<Exercicio> exercicios
    ) {

        StringBuilder listaExercicios =
            new StringBuilder();

        for (Exercicio exercicio : exercicios) {

            listaExercicios
                .append("- ")
                .append(exercicio.getNome())
                .append(" | Categoria: ")
                .append(exercicio.getCategoria())
                .append(" | Equipamento: ")
                .append(exercicio.getEquipamento())
                .append("\n");
        }

        String prompt = """
            Você é um personal trainer profissional.

            Monte uma ficha de treino inteligente.

            Objetivo:
            %s

            Dias por semana:
            %d

            Utilize SOMENTE os exercícios abaixo:

            %s

            Retorne em formato organizado contendo:

            - dia da semana
            - grupo muscular
            - exercício
            - séries
            - repetições
            - intervalo
            - instrução
            """
            .formatted(
                objetivo,
                diasPorSemana,
                listaExercicios
            );

        Map<String, Object> request =
            Map.of(
                "model", ollamaModel,
                "prompt", prompt,
                "stream", false
            );

        ResponseEntity<Map> response =
            restTemplate.postForEntity(
                ollamaUrl,
                request,
                Map.class
            );

        if (
            response.getBody() == null ||
            response.getBody().get("response") == null
        ) {

            throw new RuntimeException(
                "Resposta inválida do Ollama."
            );
        }

        return response
            .getBody()
            .get("response")
            .toString();
    }
}