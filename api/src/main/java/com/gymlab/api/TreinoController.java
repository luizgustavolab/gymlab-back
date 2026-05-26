package com.gymlab.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TreinoController {

    private final ExercicioRepository exercicioRepository;
    private final TreinoUsuarioRepository treinoUsuarioRepository;

    public TreinoController(ExercicioRepository exercicioRepository, TreinoUsuarioRepository treinoUsuarioRepository) {
        this.exercicioRepository = exercicioRepository;
        this.treinoUsuarioRepository = treinoUsuarioRepository;
    }

    // 1. Listagem pública do catálogo de exercícios
    @GetMapping("/exercicios")
    public List<Exercicio> listarExercicios() {
        return exercicioRepository.findAll();
    }

    // 2. Endpoint autenticado para salvar um treino específico
    @PostMapping("/treinos")
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoUsuario criarTreino(
            @Valid @RequestBody TreinoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String supabaseUserId = jwt.getSubject();
        
        Exercicio exercicio = exercicioRepository.findById(request.exercicioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado no catálogo"));

        TreinoUsuario treino = new TreinoUsuario();
        treino.setId(UUID.randomUUID());
        treino.setUserId(UUID.fromString(supabaseUserId));
        treino.setExercicio(exercicio);
        treino.setDiaSemana(request.diaSemana());
        treino.setGrupoMuscular(request.grupoMuscular());
        treino.setSeries(request.series());
        treino.setRepeticoes(request.repeticoes());
        treino.setIntervalo(request.intervalo());
        treino.setCriadoEm(LocalDate.now());

        return treinoUsuarioRepository.save(treino);
    }

    // 3. Nova rota para o Angular disparar o fluxo de Geração via IA
    @PostMapping("/treinos/gerar")
    @ResponseStatus(HttpStatus.CREATED)
    public List<TreinoUsuario> gerarFichaInteligente(
            @Valid @RequestBody GeraTreinoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // O ID do usuário vem direto do JWT do Supabase, garantindo segurança
        String supabaseUserId = jwt.getSubject();
        
        // TODO: Aqui entra a integração com a IA (OpenAI/Anthropic)
        // 1. Calcular IMC (request.peso / (request.altura * request.altura))
        // 2. Filtrar exercícios do catálogo baseada no request.objetivo()
        // 3. Orquestrar a montagem e persistir no treinoUsuarioRepository
        
        return Collections.emptyList(); 
    }
}

// DTO para criação de um exercício individual na ficha
record TreinoRequest(
    @NotNull(message = "O ID do exercício é obrigatório")
    UUID exercicioId,
    @NotBlank(message = "O dia da semana deve ser informado")
    String diaSemana,
    @NotBlank(message = "O grupo muscular é obrigatório")
    String grupoMuscular,
    @Min(value = 1, message = "O treino deve ter pelo menos 1 série")
    int series,
    @Min(value = 1, message = "As repetições devem ser maiores que zero")
    int repeticoes,
    String intervalo
) {}

// DTO para o formulário de perfil/cadastro que disparará a IA
record GeraTreinoRequest(
    @NotBlank(message = "O gênero deve ser informado")
    String genero,
    @Min(value = 30, message = "Peso deve ser maior que 30kg")
    double peso,
    @Min(value = 1, message = "Altura deve ser maior que 1 metro")
    double altura,
    @NotBlank(message = "O objetivo do treino é obrigatório")
    String objetivo,
    @Min(value = 1, message = "Mínimo de 1 dia")
    @Max(value = 7, message = "Máximo de 7 dias")
    int diasPorSemana,
    String feedbackAjuste
) {}