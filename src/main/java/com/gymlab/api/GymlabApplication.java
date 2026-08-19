package com.gymlab.api;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GymlabApplication {

    static {

        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

        dotenv.entries().forEach(entry ->

            System.setProperty(
                entry.getKey(),
                entry.getValue()
            )
        );

        System.out.println(
            "Variáveis .env carregadas com sucesso."
        );
    }

    public static void main(String[] args) {

        SpringApplication.run(
            GymlabApplication.class,
            args
        );
    }
}