package com.gymlab.api;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exercicios")
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_id", nullable = false, unique = true, columnDefinition = "text")
    private String externalId;

    @Column(nullable = false, columnDefinition = "text")
    private String nome;

    @Column(name = "nome_original", columnDefinition = "text")
    private String nomeOriginal;

    @Column(columnDefinition = "text")
    private String categoria;

    @Column(columnDefinition = "text")
    private String nivel;

    @Column(columnDefinition = "text")
    private String equipamento;

    @Column(columnDefinition = "text")
    private String forca;

    @Column(columnDefinition = "text")
    private String mecanica;

    @Column(columnDefinition = "text")
    private String overview;

    @Column(columnDefinition = "text")
    private String seguranca;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> instrucoes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> dicas;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "erros_comuns", columnDefinition = "jsonb")
    private List<String> errosComuns;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> variacoes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> keywords;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "musculos_primarios", columnDefinition = "jsonb")
    private List<String> musculosPrimarios;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "musculos_secundarios", columnDefinition = "jsonb")
    private List<String> musculosSecundarios;

    @Column(name = "created_at", columnDefinition = "timestamptz")
    private OffsetDateTime createdAt;

    public Exercicio() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    public String getForca() {
        return forca;
    }

    public void setForca(String forca) {
        this.forca = forca;
    }

    public String getMecanica() {
        return mecanica;
    }

    public void setMecanica(String mecanica) {
        this.mecanica = mecanica;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getSeguranca() {
        return seguranca;
    }

    public void setSeguranca(String seguranca) {
        this.seguranca = seguranca;
    }

    public List<String> getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(List<String> instrucoes) {
        this.instrucoes = instrucoes;
    }

    public List<String> getDicas() {
        return dicas;
    }

    public void setDicas(List<String> dicas) {
        this.dicas = dicas;
    }

    public List<String> getErrosComuns() {
        return errosComuns;
    }

    public void setErrosComuns(List<String> errosComuns) {
        this.errosComuns = errosComuns;
    }

    public List<String> getVariacoes() {
        return variacoes;
    }

    public void setVariacoes(List<String> variacoes) {
        this.variacoes = variacoes;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getMusculosPrimarios() {
        return musculosPrimarios;
    }

    public void setMusculosPrimarios(List<String> musculosPrimarios) {
        this.musculosPrimarios = musculosPrimarios;
    }

    public List<String> getMusculosSecundarios() {
        return musculosSecundarios;
    }

    public void setMusculosSecundarios(List<String> musculosSecundarios) {
        this.musculosSecundarios = musculosSecundarios;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}