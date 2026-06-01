package com.gymlab.api;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class HipertrofiaStrategy implements WorkoutStrategy {

    private final TemplateResolver templateResolver;

    public HipertrofiaStrategy(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    @Override
    public boolean supports(String objetivo) {
        if (objetivo == null) {
            return false;
        }

        String o = objetivo.toLowerCase();

        return o.contains("hipertrofia")
                || o.contains("massa")
                || o.contains("bulking");
    }

    @Override
    public int priority() {
        return 20;
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
        Set<String> nomesUsados = new HashSet<>();

        for (TemplateDia dia : template.dias()) {

            for (GrupoConfig grupoConfig : dia.grupos()) {

                String grupo = grupoConfig.grupo();

List<Exercicio> candidatos = ctx.getExercicios()
        .stream()
        .filter(ex -> grupo.equals(MuscleGroupMapper.mapPrimaryMuscle(ex.getMusculosPrimarios())))
        .filter(ex -> CategoriaTreinoRules.categoriaValida(ctx.getObjetivo(), ex.getCategoria()))
        .filter(ex -> {
            String norm = ExerciseNameNormalizer.normalize(ex.getNome());
            if (nomesUsados.contains(norm)) {
                return false;
            }
            nomesUsados.add(norm);
            return true;
        })
        .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    Collections.shuffle(list); 
                    return list.stream();
                }
        ))
        .limit(grupoConfig.quantidadeExercicios())
        .toList();

List<Exercicio> filtrados = new ArrayList<>();
Set<String> nomesUsadosLocal = new HashSet<>();

for (Exercicio ex : candidatos) {
    String norm = ExerciseNameNormalizer.normalize(ex.getNome());
    if (nomesUsadosLocal.add(norm)) {
        filtrados.add(ex);
    }
}

Collections.shuffle(filtrados);

List<Exercicio> selecionados = filtrados.stream()
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