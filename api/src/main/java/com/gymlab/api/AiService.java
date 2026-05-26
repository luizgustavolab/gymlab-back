package com.gymlab.api;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@Service
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public String gerarFicha(String prompt) {
        // Estrutura esperada pelo Ollama
        Map<String, Object> request = Map.of(
            "model", "llama3",
            "prompt", prompt,
            "stream", false
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(OLLAMA_URL, request, Map.class);
        
        // Retorna o texto gerado pela IA
        return (String) response.getBody().get("response");
    }
}