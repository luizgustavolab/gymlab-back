package com.gymlab.api;

public class AiResponseSanitizer {

    public static String limparJson(String resposta) {

        if (resposta == null) {
            return "[]";
        }

        return resposta
            .replace("```json", "")
            .replace("```", "")
            .trim();
    }
}