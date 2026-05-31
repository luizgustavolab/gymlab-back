package com.gymlab.api;

import java.util.List;
import java.util.Map;

public final class ExercisePriorityCatalog {

    private ExercisePriorityCatalog() {}

    public static final Map<String, List<String>> HIPERTROFIA =
            Map.of(

                    "PEITO",
                    List.of(
                            "bench press",
                            "incline bench press",
                            "dumbbell press"
                    ),

                    "COSTAS",
                    List.of(
                            "pull up",
                            "lat pulldown",
                            "barbell row"
                    ),

                    "QUADRICEPS",
                    List.of(
                            "squat",
                            "leg press",
                            "lunge"
                    )
            );

    public static List<String> getPriorityFor(String grupo) {
        return HIPERTROFIA.getOrDefault(grupo, List.of());
    }
}