package com.gymlab.api;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "treinos_usuarios")
@Data
public class TreinoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "dia_semana", nullable = false)
    private String diaSemana;

    @Column(name = "grupo_muscular", nullable = false)
    private String grupoMuscular;

    @ManyToOne
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio; // Aponta para o catálogo estático (ex: Tríceps Corda)

    private int series;          // ex: 3
    private int repeticoes;      // ex: 8
    private String intervalo;    // ex: "1 minuto"

    @Column(name = "criado_em", nullable = false)
    private LocalDate criadoEm;
}