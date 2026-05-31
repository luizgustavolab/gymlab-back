package com.gymlab.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ObjetivoTreino {

    HIPERTROFIA(1),
    EMAGRECIMENTO(2),
    FORCA(3),
    DEFINICAO(4);

    private final int codigo;

    ObjetivoTreino(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

   
    @JsonCreator
    public static ObjetivoTreino from(String value) {

        if (value == null) {
            throw new IllegalArgumentException("Objetivo não pode ser nulo");
        }

        String normalizado = value
                .trim()
                .toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U");

        return Arrays.stream(values())
                .filter(o -> o.name().equals(normalizado))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Objetivo inválido: " + value
                        )
                );
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}