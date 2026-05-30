package com.gymlab.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TreinoController {

    private final ExercicioRepository exercicioRepository;
    private final TreinoUsuarioRepository treinoUsuarioRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TreinoController(
            ExercicioRepository exercicioRepository,
            TreinoUsuarioRepository treinoUsuarioRepository,
            AiService aiService
    ) {
        this.exercicioRepository = exercicioRepository;
        this.treinoUsuarioRepository = treinoUsuarioRepository;
        this.aiService = aiService;
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

        return treinoUsuarioRepository
                .findByUserId(userId)
                .stream()
                .map(treino -> new TreinoDashboardDto(
                        treino.getDiaSemana(),
                        treino.getGrupoMuscular(),
                        treino.getExercicio().getNome(),
                        treino.getExercicio().getEquipamento(),
                        treino.getExercicio().getInstrucoes() != null && !treino.getExercicio().getInstrucoes().isEmpty()
                                ? treino.getExercicio().getInstrucoes().get(0)
                                : "",
                        treino.getSeries(),
                        treino.getRepeticoes(),
                        treino.getIntervalo()
                ))
                .toList();
    }

    @PostMapping("/treinos")
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoUsuario criarTreino(
            @Valid @RequestBody TreinoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        validarJwt(jwt);

        UUID userId = UUID.fromString(jwt.getSubject());

        Exercicio exercicio = exercicioRepository
                .findById(request.exercicioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exercício não encontrado"
                ));

        TreinoUsuario treino = new TreinoUsuario();
        treino.setId(UUID.randomUUID());
        treino.setUserId(userId);
        treino.setExercicio(exercicio);
        treino.setDiaSemana(request.diaSemana());
        treino.setGrupoMuscular(request.grupoMuscular());
        treino.setSeries(request.series());
        treino.setRepeticoes(request.repeticoes());
        treino.setIntervalo(request.intervalo());
        treino.setCriadoEm(LocalDate.now());

        return treinoUsuarioRepository.save(treino);
    }

    @PostMapping("/treinos/gerar")
    @ResponseStatus(HttpStatus.CREATED)
    public List<TreinoUsuario> gerarFichaInteligente(
            @Valid @RequestBody GeraTreinoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        validarJwt(jwt);

        try {
            UUID userId = UUID.fromString(jwt.getSubject());

            treinoUsuarioRepository.deleteByUserId(userId);

            String nivelAlvo = request.objetivo().toLowerCase().contains("iniciante")
                    ? "Iniciante"
                    : request.objetivo().toLowerCase().contains("avançado")
                    ? "Avançado"
                    : "Intermediário";

            String categoriaAlvo = "Musculação";

            List<Exercicio> exercicios = exercicioRepository
                    .findFilteredPool(nivelAlvo, categoriaAlvo, null, 45);

            if (exercicios.isEmpty()) {
                exercicios = exercicioRepository.findFilteredPool(null, null, null, 45);
            }

            if (exercicios.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Nenhum exercício encontrado após filtragem."
                );
            }

            String respostaIa = aiService.gerarFicha(
                    request.objetivo(),
                    request.diasPorSemana(),
                    exercicios
            );

            if (respostaIa == null || respostaIa.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "IA retornou resposta vazia."
                );
            }

            String jsonDaIa = AiResponseSanitizer.limparJson(respostaIa);

            JsonNode root = objectMapper.readTree(jsonDaIa);

            if (!root.isArray()) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "IA não retornou uma lista válida."
                );
            }

            List<TreinoUsuario> novosTreinos = new ArrayList<>();

            for (JsonNode node : root) {

                String exercicioExternalId = node.path("exercicioExternalId").asText(null);

                if (exercicioExternalId == null) {
                    throw new RuntimeException("IA retornou exercício sem externalId");
                }

                Exercicio exercicio = exercicioRepository
                        .findByExternalId(exercicioExternalId)
                        .orElseThrow(() -> new RuntimeException(
                                "Exercício inválido retornado pela IA: " + exercicioExternalId
                        ));

                TreinoUsuario treino = new TreinoUsuario();
                treino.setId(UUID.randomUUID());
                treino.setUserId(userId);
                treino.setExercicio(exercicio);
                treino.setGrupoMuscular(node.path("grupoMuscular").asText("Geral"));
                treino.setDiaSemana(node.path("diaSemana").asText("Segunda"));
                treino.setSeries(node.path("series").asInt(3));
                treino.setRepeticoes(node.path("repeticoes").asInt(10));
                treino.setIntervalo(node.path("intervalo").asText("60s"));
                treino.setCriadoEm(LocalDate.now());

                novosTreinos.add(treino);
            }

            return treinoUsuarioRepository.saveAll(novosTreinos);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao gerar treino IA: " + e.getMessage()
            );
        }
    }

    private void validarJwt(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "JWT inválido ou ausente."
            );
        }
    }

    record TreinoRequest(
            @NotNull(message = "O ID do exercício é obrigatório")
            UUID exercicioId,

            @NotBlank(message = "O dia da semana deve ser informado")
            String diaSemana,

            @NotBlank(message = "O grupo muscular é obrigatório")
            String grupoMuscular,

            @Min(value = 1, message = "Séries > 0")
            int series,

            @Min(value = 1, message = "Repetições > 0")
            int repeticoes,

            String intervalo
    ) {}
}