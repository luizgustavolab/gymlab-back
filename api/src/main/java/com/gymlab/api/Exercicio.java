package com.gymlab.api;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "exercicios")
@Data // Elimina todos os getters, setters, equals, hashcode e tostring em uma única anotação
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String categoria; // String pura (peito, pernas, etc). Sem enums ou arquivos extras.

    @Column(nullable = false)
    private String equipamento;

    @Column(columnDefinition = "TEXT")
    private String instrucao;
}