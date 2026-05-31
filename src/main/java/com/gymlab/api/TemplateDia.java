package com.gymlab.api;

import java.util.List;

public record TemplateDia(

        String nome,

        List<GrupoConfig> grupos

) {

    public TemplateDia {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "Nome do treino não pode ser vazio."
            );
        }

        if (grupos == null || grupos.isEmpty()) {
            throw new IllegalArgumentException(
                    "TemplateDia deve possuir ao menos um grupo muscular."
            );
        }
    }
}