package com.gymlab.api;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TreinoEngine {

    private final StrategyResolver resolver;

    public TreinoEngine(StrategyResolver resolver) {
        this.resolver = resolver;
    }

    public List<TreinoUsuario> gerar(WorkoutContext ctx) {

        List<WorkoutStrategy> strategies =
                resolver.resolve(ctx.getObjetivo());

        List<TreinoUsuario> resultado = new ArrayList<>();

        for (WorkoutStrategy strategy : strategies) {
            resultado.addAll(strategy.gerar(ctx));
        }

        return resultado;
    }
}