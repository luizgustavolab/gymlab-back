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
import java.util.stream.Collectors;
import com.gymlab.api.AiResponseSanitizer;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TreinoController {

    private final ExercicioRepository exercicioRepository;
    private final TreinoUsuarioRepository treinoUsuarioRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TreinoController(ExercicioRepository exercicioRepository, 
                            TreinoUsuarioRepository treinoUsuarioRepository,
                            AiService aiService) {
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

    UUID userId = UUID.fromString(jwt.getSubject());

    return treinoUsuarioRepository
        .findByUserId(userId)
        .stream()
        .map(treino -> new TreinoDashboardDto(
            treino.getDiaSemana(),
            treino.getGrupoMuscular(),
            treino.getExercicio().getNome(),
            treino.getExercicio().getEquipamento(),
            treino.getExercicio().getInstrucao(),
            treino.getSeries(),
            treino.getRepeticoes(),
            treino.getIntervalo()
        ))
        .toList();
}

    @PostMapping("/treinos")
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoUsuario criarTreino(@Valid @RequestBody TreinoRequest request, @AuthenticationPrincipal Jwt jwt) {
        String supabaseUserId = jwt.getSubject();
        
        Exercicio exercicio = exercicioRepository.findById(request.exercicioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));

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

    @PostMapping("/treinos/gerar")
@ResponseStatus(HttpStatus.CREATED)
public List<TreinoUsuario> gerarFichaInteligente(
        @Valid @RequestBody GeraTreinoRequest request,
        @AuthenticationPrincipal Jwt jwt
) {

    String supabaseUserId = jwt.getSubject();

    UUID userId = UUID.fromString(supabaseUserId);

    treinoUsuarioRepository.deleteByUserId(userId);

    List<String> catalogoExercicios = exercicioRepository.findAll()
        .stream()
        .map(Exercicio::getNome)
        .toList();

    String catalogoTexto = String.join(", ", catalogoExercicios);

    String prompt = String.format(
        """
        Você é um personal trainer profissional.

        Crie uma ficha de treino para:

        Objetivo: %s
        Gênero: %s
        Peso: %.2f
        Altura: %.2f
        Dias por semana: %d

        Utilize APENAS exercícios existentes neste catálogo:

        %s

        Responda EXCLUSIVAMENTE em JSON puro.

        Formato obrigatório:

        [
          {
            "exercicioNome": "Nome",
            "grupoMuscular": "PEITO",
            "series": 4,
            "repeticoes": 10,
            "intervalo": "60s",
            "diaSemana": "Segunda"
          }
        ]
        """,
        request.objetivo(),
        request.genero(),
        request.peso(),
        request.altura(),
        request.diasPorSemana(),
        catalogoTexto
    );

    try {

            String respostaIa = aiService.gerarFicha(prompt);


String jsonDaIa =
    AiResponseSanitizer.limparJson(respostaIa);

        List<FichaTreinoIaDto> listaIa =
            objectMapper.readValue(
                jsonDaIa,
                new TypeReference<List<FichaTreinoIaDto>>() {}
            );

        List<TreinoUsuario> novosTreinos =
            listaIa.stream().map(dto -> {

                Exercicio exercicio =
                    exercicioRepository
                        .findByNomeIgnoreCase(dto.exercicioNome())
                        .orElseThrow(() ->
                            new RuntimeException(
                                "Exercício inválido retornado pela IA: "
                                + dto.exercicioNome()
                            )
                        );

                TreinoUsuario treino = new TreinoUsuario();

                treino.setId(UUID.randomUUID());

                treino.setUserId(userId);

                treino.setExercicio(exercicio);

                treino.setGrupoMuscular(dto.grupoMuscular());

                treino.setDiaSemana(dto.diaSemana());

                treino.setSeries(dto.series());

                treino.setRepeticoes(dto.repeticoes());

                treino.setIntervalo(dto.intervalo());

                treino.setCriadoEm(LocalDate.now());

                return treino;

            }).toList();

        return treinoUsuarioRepository.saveAll(novosTreinos);

    } catch (Exception e) {

        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro ao gerar treino IA: " + e.getMessage()
        );
    }
}

record TreinoRequest(
    @NotNull(message = "O ID do exercício é obrigatório") UUID exercicioId,
    @NotBlank(message = "O dia da semana deve ser informado") String diaSemana,
    @NotBlank(message = "O grupo muscular é obrigatório") String grupoMuscular,
    @Min(value = 1, message = "Séries > 0") int series,
    @Min(value = 1, message = "Repetições > 0") int repeticoes,
    String intervalo
) {}}

