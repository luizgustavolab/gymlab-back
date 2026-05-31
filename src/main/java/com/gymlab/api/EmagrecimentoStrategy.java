package com.gymlab.api;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmagrecimentoStrategy implements WorkoutStrategy {

    @Override
    public boolean supports(String objetivo) {
        if (objetivo == null) {
            return false;
        }
        String o = objetivo.toLowerCase();
        return o.contains("emagrecimento") || o.contains("definição") || o.contains("definicao") || o.contains("cut");
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public List<TreinoUsuario> gerar(WorkoutContext ctx) {
        int series = 3;
        int reps = 15;

        List<TreinoUsuario> resultado = new ArrayList<>();
        Map<String, Integer> grupoCount = new HashMap<>();
        int dia = 0;

        for (Exercicio ex : ctx.getExercicios()) {
            String grupo = grupo(ex);

            if (grupoCount.getOrDefault(grupo, 0) >= 3) {
                continue;
            }

            grupoCount.put(grupo, grupoCount.getOrDefault(grupo, 0) + 1);

            TreinoUsuario treino = new TreinoUsuario();
            treino.setUserId(ctx.getUserId());
            treino.setExercicio(ex);
            treino.setGrupoMuscular(grupo);
            treino.setDiaSemana(diaSemana(dia++ % ctx.getDiasPorSemana()));
            treino.setSeries(series);
            treino.setRepeticoes(reps);
            treino.setIntervalo("30s");
            
            treino.setObjetivo(ctx.getObjetivo().name());
            treino.setDiasSemana(ctx.getDiasPorSemana());
            treino.setCreatedAt(OffsetDateTime.now());
            treino.setCriadoEm(LocalDate.now());
            treino.setEquipamento(ex.getEquipamento());
            treino.setExercicioNome(ex.getNome());
            treino.setNivel(ex.getNivel());

            if (ex.getInstrucoes() != null && !ex.getInstrucoes().isEmpty()) {
                treino.setInstrucao(ex.getInstrucoes().get(0));
            } else {
                treino.setInstrucao("");
            }

            resultado.add(treino);
        }

        return resultado;
    }

    private String grupo(Exercicio ex) {
        return MuscleGroupMapper.mapPrimaryMuscle(ex.getMusculosPrimarios());
    }

    private String diaSemana(int i) {
        return switch (i) {
            case 0 -> "Segunda";
            case 1 -> "Terça";
            case 2 -> "Quarta";
            case 3 -> "Quinta";
            case 4 -> "Sexta";
            case 5 -> "Sábado";
            default -> "Domingo";
        };
    }
}