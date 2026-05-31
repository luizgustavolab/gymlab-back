package com.gymlab.api;

import java.util.List;

public interface WorkoutStrategy {

    boolean supports(String objetivo);

    int priority();

    List<TreinoUsuario> gerar(WorkoutContext context);
}