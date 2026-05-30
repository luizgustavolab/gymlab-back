package com.gymlab.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AiResponseSanitizer {

    private static final Pattern JSON_ARRAY_PATTERN =
            Pattern.compile("\\[\\s*\\{.*\\}\\s*\\]", Pattern.DOTALL);

    public static String limparJson(String input) {

        if (input == null || input.isBlank()) {
            return "[]";
        }

        String cleaned = input
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        // 🔥 tenta extrair um JSON array válido completo
        Matcher matcher = JSON_ARRAY_PATTERN.matcher(cleaned);

        if (matcher.find()) {
            return matcher.group();
        }

        // fallback: tenta recorte manual (mais agressivo)
        int start = cleaned.indexOf("[");
        int end = cleaned.lastIndexOf("]");

        if (start != -1 && end != -1 && end > start) {
            return cleaned.substring(start, end + 1);
        }

        throw new RuntimeException(
                "JSON inválido vindo da IA. Conteúdo recebido: " + preview(cleaned)
        );
    }

    private static String preview(String input) {
        return input.length() > 300 ? input.substring(0, 300) + "..." : input;
    }
}