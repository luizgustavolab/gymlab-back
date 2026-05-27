package com.gymlab.api;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {

    private static boolean carregado = false;

    private DotenvConfig() {
        // evita instanciação
    }

    public static void carregar() {

        if (carregado) {
            return;
        }

        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

        dotenv.entries().forEach(entry ->

            System.setProperty(
                entry.getKey(),
                entry.getValue()
            )
        );

        carregado = true;

        System.out.println(
            "Variáveis do arquivo .env carregadas com sucesso."
        );
    }
}