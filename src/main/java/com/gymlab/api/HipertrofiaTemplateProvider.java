package com.gymlab.api;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HipertrofiaTemplateProvider {

    public TreinoTemplate build(
            ObjetivoTreino objetivo,
            Genero genero,
            int dias
    ) {

        return switch (objetivo) {

            case HIPERTROFIA -> hipertrofia(
                    genero,
                    dias
            );

            default -> throw new IllegalArgumentException(
                    "Template não suportado: " + objetivo
            );
        };
    }

    private TreinoTemplate hipertrofia(
            Genero genero,
            int dias
    ) {

        return switch (dias) {

            case 1 -> template1x();

            case 2 -> template2x(genero);

            case 3 -> template3x(genero);

            case 4 -> template4x(genero);

            case 5 -> template5x(genero);

            case 6 -> template6x(genero);

            case 7 -> template7x(genero);

            default -> throw new IllegalArgumentException(
                    "Dias inválidos: " + dias
            );
        };
    }

    private TreinoTemplate template1x() {

        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                1,
                null,
                List.of(
                        new TemplateDia(
                                "Full Body",
                                List.of(
                                        GrupoConfigFactory.grupo("PEITO"),
                                        GrupoConfigFactory.grupo("COSTAS"),
                                        GrupoConfigFactory.grupo("QUADRICEPS"),
                                        GrupoConfigFactory.grupo("POSTERIOR"),
                                        GrupoConfigFactory.grupo("OMBROS"),
                                        GrupoConfigFactory.grupo("BICEPS"),
                                        GrupoConfigFactory.grupo("TRICEPS"),
                                        GrupoConfigFactory.grupo("GLUTEOS"),
                                        GrupoConfigFactory.grupo("CORE"),
                                        GrupoConfigFactory.grupo("LOMBAR"),
                                        GrupoConfigFactory.grupo("PANTURRILHAS"),
                                        GrupoConfigFactory.grupo("TRAPEZIO"),
                                        GrupoConfigFactory.grupo("PESCOCO")
                                )
                        )
                )
        );
    }

    private TreinoTemplate template2x(Genero genero) {

        boolean feminino = genero == Genero.FEMININO;

        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                2,
                genero,
                List.of(
                        new TemplateDia(
                                "Treino A",
                                List.of(
                                        GrupoConfigFactory.grupo("PEITO"),
                                        GrupoConfigFactory.grupo("COSTAS"),
                                        GrupoConfigFactory.grupo("OMBROS"),
                                        GrupoConfigFactory.grupo("TRAPEZIO"),
                                        GrupoConfigFactory.grupo("BICEPS"),
                                        GrupoConfigFactory.grupo("TRICEPS"),
                                        GrupoConfigFactory.grupo("ANTEBRACO"),
                                        GrupoConfigFactory.grupo("PESCOCO"),
                                        GrupoConfigFactory.grupo("CORE")
                                )
                        ),
                        new TemplateDia(
                                "Treino B",
                                feminino
                                        ? List.of(
                                        GrupoConfigFactory.grupo("QUADRICEPS"),
                                        GrupoConfigFactory.grupo("POSTERIOR"),
                                        GrupoConfigFactory.grupo("GLUTEOS"),
                                        GrupoConfigFactory.grupo("ADUTORES"),
                                        GrupoConfigFactory.grupo("PANTURRILHAS"),
                                        GrupoConfigFactory.grupo("LOMBAR")
                                )
                                        : List.of(
                                        GrupoConfigFactory.grupo("QUADRICEPS"),
                                        GrupoConfigFactory.grupo("POSTERIOR"),
                                        GrupoConfigFactory.grupo("GLUTEOS"),
                                        GrupoConfigFactory.grupo("ADUTORES"),
                                        GrupoConfigFactory.grupo("PANTURRILHAS"),
                                        GrupoConfigFactory.grupo("CORE")
                                )
                        )
                )
        );
    }

    private TreinoTemplate template3x(Genero genero) {

        boolean feminino = genero == Genero.FEMININO;

        return feminino
                ? template3xFeminino()
                : template3xMasculino();
    }

    private TreinoTemplate template3xMasculino() {

        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                3,
                Genero.MASCULINO,
                List.of(
                        new TemplateDia(
                                "Treino A",
                                List.of(
                                        GrupoConfigFactory.grupo("PEITO"),
                                        GrupoConfigFactory.grupo("OMBROS"),
                                        GrupoConfigFactory.grupo("TRICEPS"),
                                        GrupoConfigFactory.grupo("PESCOCO")
                                )
                        ),
                        new TemplateDia(
                                "Treino B",
                                List.of(
                                        GrupoConfigFactory.grupo("COSTAS"),
                                        GrupoConfigFactory.grupo("TRAPEZIO"),
                                        GrupoConfigFactory.grupo("BICEPS"),
                                        GrupoConfigFactory.grupo("ANTEBRACO")
                                )
                        ),
                        new TemplateDia(
                                "Treino C",
                                List.of(
                                        GrupoConfigFactory.grupo("QUADRICEPS"),
                                        GrupoConfigFactory.grupo("POSTERIOR"),
                                        GrupoConfigFactory.grupo("ADUTORES"),
                                        GrupoConfigFactory.grupo("GLUTEOS"),
                                        GrupoConfigFactory.grupo("PANTURRILHAS"),
                                        GrupoConfigFactory.grupo("CORE")
                                )
                        )
                )
        );
    }

    private TreinoTemplate template3xFeminino() {

        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                3,
                Genero.FEMININO,
                List.of(
                        new TemplateDia(
                                "Treino A",
                                List.of(
                                        GrupoConfigFactory.grupo("QUADRICEPS"),
                                        GrupoConfigFactory.grupo("ADUTORES"),
                                        GrupoConfigFactory.grupo("PANTURRILHAS"),
                                        GrupoConfigFactory.grupo("PEITO")
                                )
                        ),
                        new TemplateDia(
                                "Treino B",
                                List.of(
                                        GrupoConfigFactory.grupo("COSTAS"),
                                        GrupoConfigFactory.grupo("OMBROS"),
                                        GrupoConfigFactory.grupo("BICEPS"),
                                        GrupoConfigFactory.grupo("TRICEPS"),
                                        GrupoConfigFactory.grupo("TRAPEZIO"),
                                        GrupoConfigFactory.grupo("CORE")
                                )
                        ),
                        new TemplateDia(
                                "Treino C",
                                List.of(
                                        GrupoConfigFactory.grupo("POSTERIOR"),
                                        GrupoConfigFactory.grupo("GLUTEOS"),
                                        GrupoConfigFactory.grupo("LOMBAR"),
                                        GrupoConfigFactory.grupo("PANTURRILHAS"),
                                        GrupoConfigFactory.grupo("PESCOCO")
                                )
                        )
                )
        );
    }

    private TreinoTemplate template4x(Genero genero) {

        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                4,
                genero,
                List.of(
                        new TemplateDia("Treino A", List.of(
                                GrupoConfigFactory.grupo("PEITO"),
                                GrupoConfigFactory.grupo("TRICEPS"),
                                GrupoConfigFactory.grupo("PESCOCO")
                        )),
                        new TemplateDia("Treino B", List.of(
                                GrupoConfigFactory.grupo("COSTAS"),
                                GrupoConfigFactory.grupo("TRAPEZIO"),
                                GrupoConfigFactory.grupo("ANTEBRACO")
                        )),
                        new TemplateDia("Treino C", List.of(
                                GrupoConfigFactory.grupo("QUADRICEPS"),
                                GrupoConfigFactory.grupo("POSTERIOR"),
                                GrupoConfigFactory.grupo("GLUTEOS"),
                                GrupoConfigFactory.grupo("ADUTORES"),
                                GrupoConfigFactory.grupo("PANTURRILHAS")
                        )),
                        new TemplateDia("Treino D", List.of(
                                GrupoConfigFactory.grupo("OMBROS"),
                                GrupoConfigFactory.grupo("BICEPS"),
                                GrupoConfigFactory.grupo("CORE")
                        ))
                )
        );
    }

    private TreinoTemplate template5x(Genero genero) {

        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                5,
                genero,
                List.of(
                        new TemplateDia("Treino A", List.of(
                                GrupoConfigFactory.grupo("PEITO"),
                                GrupoConfigFactory.grupo("PESCOCO")
                        )),
                        new TemplateDia("Treino B", List.of(
                                GrupoConfigFactory.grupo("COSTAS"),
                                GrupoConfigFactory.grupo("TRAPEZIO")
                        )),
                        new TemplateDia("Treino C", List.of(
                                GrupoConfigFactory.grupo("QUADRICEPS"),
                                GrupoConfigFactory.grupo("POSTERIOR"),
                                GrupoConfigFactory.grupo("GLUTEOS"),
                                GrupoConfigFactory.grupo("ADUTORES")
                        )),
                        new TemplateDia("Treino D", List.of(
                                GrupoConfigFactory.grupo("OMBROS"),
                                GrupoConfigFactory.grupo("CORE")
                        )),
                        new TemplateDia("Treino E", List.of(
                                GrupoConfigFactory.grupo("BICEPS"),
                                GrupoConfigFactory.grupo("TRICEPS"),
                                GrupoConfigFactory.grupo("ANTEBRACO"),
                                GrupoConfigFactory.grupo("PANTURRILHAS")
                        ))
                )
        );
    }

    private TreinoTemplate template6x(Genero genero) {
        return genero == Genero.FEMININO
                ? template6xFeminino()
                : template6xMasculino();
    }

    private TreinoTemplate template6xMasculino() {
        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                6,
                Genero.MASCULINO,
                List.of(
                        new TemplateDia("Treino A", List.of(
                                GrupoConfigFactory.grupo("PEITO"),
                                GrupoConfigFactory.grupo("OMBROS"),
                                GrupoConfigFactory.grupo("TRICEPS"),
                                GrupoConfigFactory.grupo("PESCOCO")
                        )),
                        new TemplateDia("Treino B", List.of(
                                GrupoConfigFactory.grupo("COSTAS"),
                                GrupoConfigFactory.grupo("TRAPEZIO"),
                                GrupoConfigFactory.grupo("BICEPS"),
                                GrupoConfigFactory.grupo("ANTEBRACO")
                        )),
                        new TemplateDia("Treino C", List.of(
                                GrupoConfigFactory.grupo("QUADRICEPS"),
                                GrupoConfigFactory.grupo("POSTERIOR"),
                                GrupoConfigFactory.grupo("ADUTORES"),
                                GrupoConfigFactory.grupo("GLUTEOS"),
                                GrupoConfigFactory.grupo("PANTURRILHAS"),
                                GrupoConfigFactory.grupo("CORE")
                        ))
                )
        );
    }

    private TreinoTemplate template6xFeminino() {
        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                6,
                Genero.FEMININO,
                List.of(
                        new TemplateDia("Treino A", List.of(
                                GrupoConfigFactory.grupo("QUADRICEPS"),
                                GrupoConfigFactory.grupo("ADUTORES"),
                                GrupoConfigFactory.grupo("PANTURRILHAS"),
                                GrupoConfigFactory.grupo("CORE")
                        )),
                        new TemplateDia("Treino B", List.of(
                                GrupoConfigFactory.grupo("PEITO"),
                                GrupoConfigFactory.grupo("COSTAS"),
                                GrupoConfigFactory.grupo("OMBROS"),
                                GrupoConfigFactory.grupo("TRAPEZIO"),
                                GrupoConfigFactory.grupo("BICEPS"),
                                GrupoConfigFactory.grupo("TRICEPS"),
                                GrupoConfigFactory.grupo("ANTEBRACO"),
                                GrupoConfigFactory.grupo("PESCOCO")
                        )),
                        new TemplateDia("Treino C", List.of(
                                GrupoConfigFactory.grupo("POSTERIOR"),
                                GrupoConfigFactory.grupo("GLUTEOS"),
                                GrupoConfigFactory.grupo("LOMBAR"),
                                GrupoConfigFactory.grupo("PANTURRILHAS")
                        ))
                )
        );
    }

    private TreinoTemplate template7x(Genero genero) {

        return new TreinoTemplate(
                ObjetivoTreino.HIPERTROFIA,
                7,
                genero,
                List.of(
                        new TemplateDia("Treino A", List.of(
                                GrupoConfigFactory.grupo("PEITO"),
                                GrupoConfigFactory.grupo("PESCOCO")
                        )),
                        new TemplateDia("Treino B", List.of(
                                GrupoConfigFactory.grupo("COSTAS"),
                                GrupoConfigFactory.grupo("TRAPEZIO")
                        )),
                        new TemplateDia("Treino C", List.of(
                                GrupoConfigFactory.grupo("QUADRICEPS"),
                                GrupoConfigFactory.grupo("ADUTORES")
                        )),
                        new TemplateDia("Treino D", List.of(
                                GrupoConfigFactory.grupo("OMBROS"),
                                GrupoConfigFactory.grupo("CORE")
                        )),
                        new TemplateDia("Treino E", List.of(
                                GrupoConfigFactory.grupo("POSTERIOR"),
                                GrupoConfigFactory.grupo("GLUTEOS"),
                                GrupoConfigFactory.grupo("LOMBAR")
                        )),
                        new TemplateDia("Treino F", List.of(
                                GrupoConfigFactory.grupo("BICEPS"),
                                GrupoConfigFactory.grupo("TRICEPS"),
                                GrupoConfigFactory.grupo("ANTEBRACO")
                        )),
                        new TemplateDia("Treino G", List.of(
                                GrupoConfigFactory.grupo("PANTURRILHAS"),
                                GrupoConfigFactory.grupo("CORE")
                        ))
                )
        );
    }
}