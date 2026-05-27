package com.gymlab.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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

            List<Exercicio> exerciciosPadrao = Arrays.asList(
                // PEITO
                criarExercicio("Supino Reto com Barra", "PEITO", "Banco Horizontal e Barra", "Deite no banco, desça a barra até o peito e empurre verticalmente."),
                criarExercicio("Supino Inclinado com Halteres", "PEITO", "Banco Inclinado e Halteres", "Com o banco a 45 graus, empurre os halteres para cima alinhando-os na descida."),
                criarExercicio("Crucifixo Máquina (Peck Deck)", "PEITO", "Máquina Peck Deck", "Sente-se no aparelho e feche os braços contra a resistência, mantendo os cotovelos semiflexionados."),
                criarExercicio("Crossover na Polia", "PEITO", "Polia / Crossover", "Puxe os cabos de cima para baixo cruzando as mãos à frente do corpo."),

                // COSTAS
                criarExercicio("Puxada Alta na Polia", "COSTAS", "Pulley", "Puxe a barra em direção ao peito inclinando levemente o tronco para trás."),
                criarExercicio("Remada Curvada com Barra", "COSTAS", "Barra e Anilhas", "Incline o tronco à frente e puxe a barra em direção ao abdômen, retraindo as escápulas."),
                criarExercicio("Remada Baixa Sentado", "COSTAS", "Polia Baixa", "Puxe o puxador triângulo em direção à cintura mantendo a postura reta."),

                // PERNAS
                criarExercicio("Agachamento Livre", "PERNAS", "Barra e Anilhas", "Mantenha os pés afastados na largura dos ombros e desça o quadril como se fosse sentar."),
                criarExercicio("Leg Press 45 Graus", "PERNAS", "Máquina Leg Press", "Empurre a plataforma com os pés, flexionando e estendendo os joelhos de forma controlada."),
                criarExercicio("Cadeira Extensora", "PERNAS", "Máquina Extensora", "Estenda os joelhos levantando a almofada de peso e segure a contração no topo."),

                // OMBROS
                criarExercicio("Desenvolvimento com Halteres", "OMBROS", "Halteres e Banco", "Empurre os halteres acima da cabeça até estender quase completamente os braços."),
                criarExercicio("Elevação Lateral com Halteres", "OMBROS", "Halteres", "Eleve os braços lateralmente até a altura dos ombros, com uma leve flexão nos cotovelos."),

                // BÍCEPS
                criarExercicio("Rosca Direta na Polia", "BICEPS", "Crossover / Polia Baixa", "Segure a barra flutuante e flexione os cotovelos trazendo a carga para cima."),
                criarExercicio("Rosca Alternada com Halteres", "BICEPS", "Halteres", "Flexione um braço de cada vez girando o punho durante a subida."),
                
                // TRÍCEPS
                criarExercicio("Tríceps Corda", "TRICEPS", "Polia Alta com Corda", "Estenda completamente os cotovelos para baixo, abrindo a corda no final do movimento."),
                criarExercicio("Tríceps Testa com Barra", "TRICEPS", "Banco Horizontal e Barra W", "Deitado, flexione os cotovelos descendo a barra até a testa e estenda novamente.")
            );

            exercicioRepository.saveAll(exerciciosPadrao);
            System.out.println("Catálogo inicial de exercícios salvo com sucesso!");
        }
    }

    private Exercicio criarExercicio(String nome, String categoria, String equipamento, String instrucao) {
        Exercicio exercicio = new Exercicio();
        exercicio.setNome(nome);
        exercicio.setCategoria(categoria);
        exercicio.setEquipamento(equipamento);
        exercicio.setInstrucao(instrucao);
        return exercicio;
    }
}