package com.gymlab.api;

import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TreinoController {

    private final ExercicioRepository exercicioRepository;

    private final TreinoUsuarioRepository treinoUsuarioRepository;

    private final AiService aiService;

    private final ObjectMapper objectMapper =
        new ObjectMapper();

    public TreinoController(
        ExercicioRepository exercicioRepository,
        TreinoUsuarioRepository treinoUsuarioRepository,
        AiService aiService
    ) {

        this.exercicioRepository =
            exercicioRepository;

        this.treinoUsuarioRepository =
            treinoUsuarioRepository;

        this.aiService =
            aiService;
    }

    // =====================================================
    // LISTAR EXERCÍCIOS
    // =====================================================

    @GetMapping("/exercicios")
    public List<Exercicio> listarExercicios() {

        return exercicioRepository.findAll();
    }

    // =====================================================
    // BUSCAR TREINO DO USUÁRIO
    // =====================================================

    @GetMapping("/treinos/me")
    public List<TreinoDashboardDto> buscarMeuTreino(
        @AuthenticationPrincipal Jwt jwt
    ) {

        validarJwt(jwt);

        UUID userId =
            UUID.fromString(jwt.getSubject());

        return treinoUsuarioRepository
            .findByUserId(userId)
            .stream()
            .map(treino ->
                new TreinoDashboardDto(

                    treino.getDiaSemana(),

                    treino.getGrupoMuscular(),

                    treino
                        .getExercicio()
                        .getNome(),

                    treino
                        .getExercicio()
                        .getEquipamento(),

                    treino
                        .getExercicio()
                        .getInstrucao(),

                    treino.getSeries(),

                    treino.getRepeticoes(),

                    treino.getIntervalo()
                )
            )
            .toList();
    }

    // =====================================================
    // CRIAR TREINO MANUAL
    // =====================================================

    @PostMapping("/treinos")
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoUsuario criarTreino(
        @Valid @RequestBody TreinoRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {

        validarJwt(jwt);

        UUID userId =
            UUID.fromString(jwt.getSubject());

        Exercicio exercicio =
            exercicioRepository
                .findById(request.exercicioId())
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exercício não encontrado"
                    )
                );

        TreinoUsuario treino =
            new TreinoUsuario();

        treino.setId(UUID.randomUUID());

        treino.setUserId(userId);

        treino.setExercicio(exercicio);

        treino.setDiaSemana(
            request.diaSemana()
        );

        treino.setGrupoMuscular(
            request.grupoMuscular()
        );

        treino.setSeries(
            request.series()
        );

        treino.setRepeticoes(
            request.repeticoes()
        );

        treino.setIntervalo(
            request.intervalo()
        );

        treino.setCriadoEm(
            LocalDate.now()
        );

        return treinoUsuarioRepository
            .save(treino);
    }

    // =====================================================
    // GERAR TREINO IA
    // =====================================================

    @PostMapping("/treinos/gerar")
    @ResponseStatus(HttpStatus.CREATED)
    public List<TreinoUsuario> gerarFichaInteligente(
        @Valid @RequestBody GeraTreinoRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {

        validarJwt(jwt);

        try {

            System.out.println(
                "JWT RECEBIDO:"
            );

            System.out.println(
                jwt.getTokenValue()
            );

            UUID userId =
                UUID.fromString(
                    jwt.getSubject()
                );

            // =============================================
            // REMOVE TREINOS ANTIGOS
            // =============================================

            treinoUsuarioRepository
                .deleteByUserId(userId);

            // =============================================
            // BUSCA CATÁLOGO DE EXERCÍCIOS
            // =============================================

            List<Exercicio> exercicios =
                exercicioRepository.findAll();

            if (exercicios.isEmpty()) {

                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nenhum exercício cadastrado."
                );
            }

            // =============================================
            // GERA TREINO IA
            // =============================================

            String respostaIa =
                aiService.gerarFicha(
                    request.objetivo(),
                    request.diasPorSemana(),
                    exercicios
                );

            if (
                respostaIa == null
                || respostaIa.isBlank()
            ) {

                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "IA retornou resposta vazia."
                );
            }

            // =============================================
            // SANITIZA JSON
            // =============================================

            String jsonDaIa =
                AiResponseSanitizer
                    .limparJson(respostaIa);

            System.out.println(
                "JSON LIMPO DA IA:"
            );

            System.out.println(
                jsonDaIa
            );

            // =============================================
            // CONVERTE JSON -> DTO
            // =============================================

            List<FichaTreinoIaDto> listaIa =
                objectMapper.readValue(
                    jsonDaIa,
                    new TypeReference<
                        List<FichaTreinoIaDto>
                    >() {}
                );

            if (listaIa.isEmpty()) {

                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "IA não retornou exercícios."
                );
            }

            // =============================================
            // CONVERTE DTO -> ENTITY
            // =============================================

            List<TreinoUsuario> novosTreinos =
                listaIa.stream().map(dto -> {

                    Exercicio exercicio =
                        exercicioRepository
                            .findByNomeIgnoreCase(
                                dto.exercicioNome()
                            )
                            .orElseThrow(() ->
                                new RuntimeException(
                                    "Exercício inválido retornado pela IA: "
                                        + dto.exercicioNome()
                                )
                            );

                    TreinoUsuario treino =
                        new TreinoUsuario();

                    treino.setId(
                        UUID.randomUUID()
                    );

                    treino.setUserId(
                        userId
                    );

                    treino.setExercicio(
                        exercicio
                    );

                    treino.setGrupoMuscular(
                        dto.grupoMuscular()
                    );

                    treino.setDiaSemana(
                        dto.diaSemana()
                    );

                    treino.setSeries(
                        dto.series()
                    );

                    treino.setRepeticoes(
                        dto.repeticoes()
                    );

                    treino.setIntervalo(
                        dto.intervalo()
                    );

                    treino.setCriadoEm(
                        LocalDate.now()
                    );

                    return treino;

                }).toList();

            // =============================================
            // SALVA TREINOS
            // =============================================

            return treinoUsuarioRepository
                .saveAll(novosTreinos);

        } catch (ResponseStatusException e) {

            throw e;

        } catch (Exception e) {

            e.printStackTrace();

            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao gerar treino IA: "
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // VALIDA JWT
    // =====================================================

    private void validarJwt(Jwt jwt) {

        if (jwt == null) {

            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "JWT inválido ou ausente."
            );
        }
    }

    // =====================================================
    // DTO TREINO MANUAL
    // =====================================================

    record TreinoRequest(

        @NotNull(
            message =
                "O ID do exercício é obrigatório"
        )
        UUID exercicioId,

        @NotBlank(
            message =
                "O dia da semana deve ser informado"
        )
        String diaSemana,

        @NotBlank(
            message =
                "O grupo muscular é obrigatório"
        )
        String grupoMuscular,

        @Min(
            value = 1,
            message = "Séries > 0"
        )
        int series,

        @Min(
            value = 1,
            message = "Repetições > 0"
        )
        int repeticoes,

        String intervalo
    ) {}
}