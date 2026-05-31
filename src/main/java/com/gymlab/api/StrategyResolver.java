package com.gymlab.api;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class StrategyResolver {

    private final List<WorkoutStrategy> strategies;

    public StrategyResolver(List<WorkoutStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<WorkoutStrategy> resolve(ObjetivoTreino objetivo) {

        List<WorkoutStrategy> matched = strategies.stream()
                .filter(s -> s.supports(objetivo.name()))
                .sorted(Comparator.comparingInt(WorkoutStrategy::priority).reversed())
                .toList();

        if (matched.isEmpty()) {
            throw new RuntimeException("Nenhuma strategy encontrada");
        }

        return matched;
    }
}