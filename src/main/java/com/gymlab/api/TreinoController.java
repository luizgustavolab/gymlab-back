package com.gymlab.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TreinoController {

    private static final Logger log = LoggerFactory.getLogger(TreinoController.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final ExercicioRepository exercicioRepository;
    private final TreinoUsuarioRepository treinoUsuarioRepository;
    private final TreinoEngine treinoEngine;

    public TreinoController(
            ExercicioRepository exercicioRepository,
            TreinoUsuarioRepository treinoUsuarioRepository,
            TreinoEngine treinoEngine
    ) {
        this.exercicioRepository = exercicioRepository;
        this.treinoUsuarioRepository = treinoUsuarioRepository;
        this.treinoEngine = treinoEngine;
    }

    @GetMapping("/exercicios")
    public List<Exercicio> listarExercicios() {
        return exercicioRepository.findAll();
    }

    @GetMapping("/treinos/me")
    public List<TreinoDashboardDto> buscarMeuTreino(
            @AuthenticationPrincipal Jwt jwt
    ) {
        validarJwt(jwt);

        UUID userId = UUID.fromString(jwt.getSubject());

        return treinoUsuarioRepository.findByUserId(userId)
                .stream()
                .map(this::toDashboardDto)
                .toList();
    }

    @PostMapping("/treinos")
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoDashboardDto criarTreino(
            @Valid @RequestBody TreinoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        validarJwt(jwt);

        UUID userId = UUID.fromString(jwt.getSubject());

        Exercicio exercicio = exercicioRepository.findById(request.exercicioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exercício não encontrado"
                ));

        TreinoUsuario treino = new TreinoUsuario();
        treino.setUserId(userId);
        treino.setExercicio(exercicio);
        treino.setDiaSemana(request.diaSemana());
        treino.setGrupoMuscular(request.grupoMuscular());
        treino.setSeries(request.series());
        treino.setRepeticoes(request.repeticoes());
        treino.setIntervalo(request.intervalo());
        treino.setCriadoEm(LocalDate.now());

        TreinoUsuario salvo = treinoUsuarioRepository.save(treino);

        return toDashboardDto(salvo);
    }

    @PostMapping("/treinos/gerar")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional // 🔥 AQUI ESTÁ A CORREÇÃO ESSENCIAL
    public List<TreinoDashboardDto> gerarFicha(
            @Valid @RequestBody GeraTreinoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        validarJwt(jwt);

        UUID userId = UUID.fromString(jwt.getSubject());

        // 🔥 agora o delete funciona dentro de transação
        treinoUsuarioRepository.deleteByUserId(userId);

        List<Exercicio> exercicios = exercicioRepository.findAll();

        if (exercicios.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nenhum exercício encontrado"
            );
        }

        WorkoutContext context = new WorkoutContext(
                userId,
                request.genero(),
                request.objetivo(),
                request.diasPorSemana(),
                exercicios
        );

        List<TreinoUsuario> treinosGerados = treinoEngine.gerar(context);

        treinoUsuarioRepository.saveAll(treinosGerados);

        return treinosGerados.stream()
                .map(this::toDashboardDto)
                .toList();
    }

    private void validarJwt(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "JWT inválido ou ausente."
            );
        }
    }

    private TreinoDashboardDto toDashboardDto(TreinoUsuario treino) {
        Exercicio ex = treino.getExercicio();

        return new TreinoDashboardDto(
                treino.getDiaSemana(),
                treino.getGrupoMuscular(),
                ex.getNome(),
                ex.getEquipamento(),
                primeiraInstrucao(ex),
                treino.getSeries(),
                treino.getRepeticoes(),
                treino.getIntervalo()
        );
    }

    private String primeiraInstrucao(Exercicio ex) {
        return ex.getInstrucoes() != null && !ex.getInstrucoes().isEmpty()
                ? ex.getInstrucoes().get(0)
                : "";
    }

    record TreinoRequest(
            @NotNull UUID exercicioId,
            @NotBlank String diaSemana,
            @NotBlank String grupoMuscular,
            @Min(1) int series,
            @Min(1) int repeticoes,
            String intervalo
    ) {}
}