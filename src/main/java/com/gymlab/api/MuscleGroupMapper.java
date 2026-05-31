package com.gymlab.api;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class MuscleGroupMapper {

    private static final Map<String, String> MAP = Map.ofEntries(

            Map.entry("peitoral principal cabeça esternal", "PEITO"),
            Map.entry("peitoral maior cabeça clavicular", "PEITO"),
            Map.entry("peitoral menor", "PEITO"),
            Map.entry("serratus anterior", "PEITO"),

            Map.entry("latissimus dorsi", "COSTAS"),
            Map.entry("rombóides", "COSTAS"),
            Map.entry("teres major", "COSTAS"),
            Map.entry("Teres minor", "COSTAS"),
            Map.entry("infraespinato", "COSTAS"),

            Map.entry("deltóide anterior", "OMBROS"),
            Map.entry("deltóide lateral", "OMBROS"),
            Map.entry("deltóide posterior", "OMBROS"),
            Map.entry("manguito rotador", "OMBROS"),

            Map.entry("trapézio superior", "TRAPEZIO"),
            Map.entry("trapézio médio", "TRAPEZIO"),
            Map.entry("trapézio inferior", "TRAPEZIO"),
            Map.entry("elevador escápula", "TRAPEZIO"),

            Map.entry("bíceps brachii cabeça longa", "BICEPS"),
            Map.entry("bíceps brachii cabeça curta", "BICEPS"),
            Map.entry("brachialis", "BICEPS"),

            Map.entry("brachioradialis", "ANTEBRAÇO"),
            Map.entry("flexores de pulso", "ANTEBRAÇO"),
            Map.entry("extensores de pulso", "ANTEBRAÇO"),

            Map.entry("tríceps brachii cabeça longa", "TRICEPS"),
            Map.entry("cabeça lateral de tríceps brachii", "TRICEPS"),
            Map.entry("tríceps braquial cabeça medial", "TRICEPS"),

            Map.entry("rectus abdominis", "CORE"),
            Map.entry("abdóminios transversos", "CORE"),
            Map.entry("oblíquos externos", "CORE"),
            Map.entry("oblíquos internos", "CORE"),
            Map.entry("intercostais", "CORE"),

            Map.entry("reto femoral", "QUADRICEPS"),
            Map.entry("vasto lateral", "QUADRICEPS"),
            Map.entry("vasto intermedio", "QUADRICEPS"),
            Map.entry("vaus medialis", "QUADRICEPS"),
            Map.entry("quadríceps", "QUADRICEPS"),
            Map.entry("iliopsoas", "QUADRICEPS"),
            Map.entry("flexores da anca", "QUADRICEPS"),

            Map.entry("bíceps femoral", "POSTERIOR"),
            Map.entry("semitendinoso", "POSTERIOR"),
            Map.entry("semimembranoso", "POSTERIOR"),
            Map.entry("isquiotibiais", "POSTERIOR"),

            Map.entry("glúteo maximus", "GLUTEOS"),
            Map.entry("glúteo médio", "GLUTEOS"),
            Map.entry("glúteo minimo", "GLUTEOS"),
            Map.entry("tensor fasciae latae", "GLUTEOS"),

            Map.entry("gastrocnêmio", "PANTURRILHAS"),
            Map.entry("sóleo", "PANTURRILHAS"),
            Map.entry("tibial anterior", "PANTURRILHAS"),

            Map.entry("erector espinale", "LOMBAR"),
            Map.entry("tetratus lumborum", "LOMBAR"),

            Map.entry("adductor longus", "ADUTORES"),
            Map.entry("adductor magnus", "ADUTORES"),
            Map.entry("gracilis", "ADUTORES"),

            Map.entry("esternocleidomastoide", "PESCOCO"),
            Map.entry("extensores do pescoço", "PESCOCO")
    );

    private MuscleGroupMapper() {
    }

    public static String map(String muscle) {

        if (muscle == null || muscle.isBlank()) {
            return "OUTROS";
        }

        return MAP.getOrDefault(muscle, "OUTROS");
    }

    public static Set<String> mapAll(List<String> muscles) {

        if (muscles == null || muscles.isEmpty()) {
            return Set.of();
        }

        return muscles.stream()
                .map(MuscleGroupMapper::map)
                .filter(group -> !"OUTROS".equals(group))
                .collect(Collectors.toSet());
    }

    public static Set<String> mapExercise(
            List<String> primary,
            List<String> secondary) {

        Set<String> groups = new HashSet<>();

        groups.addAll(mapAll(primary));
        groups.addAll(mapAll(secondary));

        if (groups.isEmpty()) {
            groups.add("OUTROS");
        }

        return groups;
    }

    public static String mapPrimaryMuscle(List<String> primaryMuscles) {

        if (primaryMuscles == null || primaryMuscles.isEmpty()) {
            return "OUTROS";
        }

        for (String muscle : primaryMuscles) {

            String group = map(muscle);

            if (!"OUTROS".equals(group)) {
                return group;
            }
        }

        return "OUTROS";
    }
}