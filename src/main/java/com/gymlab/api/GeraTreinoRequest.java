package com.gymlab.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.gymlab.api.Genero;
import com.gymlab.api.ObjetivoTreino;

public record GeraTreinoRequest(

    @NotNull(message = "O gênero deve ser informado")
    Genero genero,

    @DecimalMin(value = "30.0", message = "Peso deve ser maior que 30kg")
    @DecimalMax(value = "300.0", message = "Peso inválido")
    double peso,

    @DecimalMin(value = "1.00", message = "Altura deve ser maior que 1 metro")
    @DecimalMax(value = "2.50", message = "Altura inválida")
    double altura,

    @NotNull(message = "O objetivo do treino é obrigatório")
    ObjetivoTreino objetivo,

    @Min(value = 1, message = "Mínimo de 1 dia de treino por semana")
    @Max(value = 7, message = "Máximo de 7 dias de treino por semana")
    int diasPorSemana,

    @Size(max = 1000, message = "Feedback muito longo")
    String feedbackAjuste

) {}