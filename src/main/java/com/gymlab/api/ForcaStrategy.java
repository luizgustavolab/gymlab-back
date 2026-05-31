package com.gymlab.api;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ForcaStrategy implements WorkoutStrategy {

    private final TemplateResolver templateResolver;

    public ForcaStrategy(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    @Override
    public boolean supports(String objetivo) {
        if (objetivo == null) {
            return false;
        }
        String o = objetivo.toLowerCase();
        return o.contains("força") || o.contains("forca") || o.contains("strength");
    }

    @Override
    public int priority() {
        return 30;
    }

    @Override
    public List<TreinoUsuario> gerar(WorkoutContext ctx) {
        TreinoTemplate template = templateResolver.resolve(
                ctx.getObjetivo(),
                ctx.getGenero(),
                ctx.getDiasPorSemana()
        );

        List<TreinoUsuario> resultado = new ArrayList<>();
        Set<String> exerciciosJaUtilizados = new HashSet<>();

        for (TemplateDia dia : template.dias()) {
            for (GrupoConfig grupoConfig : dia.grupos()) {
                String grupo = grupoConfig.grupo();

                List<Exercicio> candidatos = ctx.getExercicios()
                        .stream()
                        .filter(ex -> grupo.equals(MuscleGroupMapper.mapPrimaryMuscle(ex.getMusculosPrimarios())))
                        .filter(ex -> !exerciciosJaUtilizados.contains(ex.getId().toString()))
                        .limit(grupoConfig.quantidadeExercicios())
                        .toList();

                for (Exercicio ex : candidatos) {
                    exerciciosJaUtilizados.add(ex.getId().toString());

                    TreinoUsuario treino = new TreinoUsuario();
                    treino.setUserId(ctx.getUserId());
                    treino.setExercicio(ex);
                    treino.setGrupoMuscular(grupo);
                    treino.setDiaSemana(dia.nome());
                    treino.setSeries(grupoConfig.series());
                    treino.setRepeticoes(grupoConfig.repeticoes());
                    treino.setIntervalo(grupoConfig.intervalo());
                    
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
            }
        }

        return resultado;
    }
}