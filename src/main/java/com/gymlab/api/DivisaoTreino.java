package com.gymlab.api;

import java.util.List;

public record DivisaoTreino(
        String nome,
        List<String> gruposMusculares
) {
}