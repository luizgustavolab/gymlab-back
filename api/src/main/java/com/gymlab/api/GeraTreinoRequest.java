package com.gymlab.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GeraTreinoRequest(
    @NotBlank(message = "O gênero deve ser informado")
    String genero,

    @Min(value = 30, message = "Peso deve ser maior que 30kg")
    double peso,

    @Min(value = 1, message = "Altura deve ser maior que 1 metro")
    double altura,

    @NotBlank(message = "O objetivo do treino é obrigatório")
    String objetivo, // hipertrofia, força, definicao, emagrecimento

    @Min(value = 1, message = "Mínimo de 1 dia de treino por semana")
    @Max(value = 7, message = "Máximo de 7 dias de treino por semana")
    int diasPorSemana,

    String feedbackAjuste // Opcional: usado quando ele altera a ficha no perfil pedindo ajustes
) {}