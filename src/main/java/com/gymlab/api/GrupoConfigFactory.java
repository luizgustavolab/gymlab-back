package com.gymlab.api;

public final class GrupoConfigFactory {

    private GrupoConfigFactory() {
    }

    public static GrupoConfig grupo(String nome) {

        return switch (nome) {

            case "TRAPEZIO" ->
                    new GrupoConfig(
                            nome,
                            VolumeConfig.exercicios(nome),
                            3,
                            12,
                            "45s"
                    );

            case "PESCOCO" ->
                    new GrupoConfig(
                            nome,
                            VolumeConfig.exercicios(nome),
                            2,
                            15,
                            "30s"
                    );

            case "CORE" ->
                    new GrupoConfig(
                            nome,
                            VolumeConfig.exercicios(nome),
                            3,
                            15,
                            "45s"
                    );

            case "PANTURRILHAS" ->
                    new GrupoConfig(
                            nome,
                            VolumeConfig.exercicios(nome),
                            4,
                            15,
                            "45s"
                    );

            default ->
                    new GrupoConfig(
                            nome,
                            VolumeConfig.exercicios(nome),
                            4,
                            10,
                            "60s"
                    );
        };
    }
}