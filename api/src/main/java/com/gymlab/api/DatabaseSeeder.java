package com.gymlab.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ExercicioRepository exercicioRepository;

    public DatabaseSeeder(ExercicioRepository exercicioRepository) {
        this.exercicioRepository = exercicioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (exercicioRepository.count() == 0) {
            System.out.println("Populando base estática de exercícios no Supabase...");

            Exercicio e1 = new Exercicio();
            e1.setNome("Supino Reto com Barra");
            e1.setCategoria("PEITO");
            e1.setEquipamento("Banco Horizontal e Barra");
            e1.setInstrucao("Deite no banco, desça a barra até o peito e empurre verticalmente.");

            Exercicio e2 = new Exercicio();
            e2.setNome("Puxada Alta na Polia");
            e2.setCategoria("COSTAS");
            e2.setEquipamento("Pulley");
            e2.setInstrucao("Puxe a barra em direção ao peito inclinado levemente o tronco para trás.");

            Exercicio e3 = new Exercicio();
            e3.setNome("Agachamento Livre");
            e3.setCategoria("PERNAS");
            e3.setEquipamento("Barra e Anilhas");
            e3.setInstrucao("Mantenha os pés afastados na largura dos ombros e desça o quadril como se fosse sentar.");

            Exercicio e4 = new Exercicio();
            e4.setNome("Rosca Direta na Polia");
            e4.setCategoria("BICEPS");
            e4.setEquipamento("Crossover / Polia");
            e4.setInstrucao("Segure a barra flutuante e flexione os cotovelos trazendo a carga para cima.");

            Exercicio e5 = new Exercicio();
            e5.setNome("Tríceps Corda");
            e5.setCategoria("TRICEPS");
            e5.setEquipamento("Polia Alta");
            e5.setInstrucao("Estenda completamente os cotovelos para baixo, abrindo a corda no final do movimento.");

            exercicioRepository.saveAll(List.of(e1, e2, e3, e4, e5));
            System.out.println("Catálogo inicial de exercícios salvo com sucesso!");
        }
    }
}