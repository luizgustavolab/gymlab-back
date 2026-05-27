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
    public List<TreinoUsuario> gerarFichaInteligente(@Valid @RequestBody GeraTreinoRequest request, @AuthenticationPrincipal Jwt jwt) {
        String supabaseUserId = jwt.getSubject();

        String prompt = String.format(
            "Crie um treino de %d dias para objetivo %s. " +
            "Responda EXATAMENTE em formato JSON (lista de objetos): " +
            "[{\"exercicioNome\": \"Nome\", \"grupoMuscular\": \"Nome\", \"series\": 3, \"repeticoes\": 10, \"intervalo\": \"60s\", \"diaSemana\": \"Segunda\"}]. " +
            "Use nomes de exercícios do catálogo: Supino Reto com Barra, Puxada Alta na Polia, Agachamento Livre, Rosca Direta na Polia, Tríceps Corda. " +
            "Não adicione textos explicativos, retorne apenas o JSON puro.",
            request.diasPorSemana(), request.objetivo()
        );

        try {
            String jsonDaIa = aiService.gerarFicha(prompt);
            List<FichaTreinoIaDto> listaIa = objectMapper.readValue(jsonDaIa, new TypeReference<List<FichaTreinoIaDto>>(){});

            List<TreinoUsuario> novosTreinos = listaIa.stream().map(dto -> {
                Exercicio ex = exercicioRepository.findAll().stream()
                    .filter(e -> e.getNome().equalsIgnoreCase(dto.exercicioNome()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Exercício não encontrado no catálogo: " + dto.exercicioNome()));

                TreinoUsuario t = new TreinoUsuario();
                t.setId(UUID.randomUUID());
                t.setUserId(UUID.fromString(supabaseUserId));
                t.setExercicio(ex);
                t.setGrupoMuscular(dto.grupoMuscular());
                t.setDiaSemana(dto.diaSemana());
                t.setSeries(dto.series());
                t.setRepeticoes(dto.repeticoes());
                t.setIntervalo(dto.intervalo());
                t.setCriadoEm(LocalDate.now());
                return t;
            }).collect(Collectors.toList());

            return treinoUsuarioRepository.saveAll(novosTreinos);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro na geração: " + e.getMessage());
        }
    }
}

record TreinoRequest(
    @NotNull(message = "O ID do exercício é obrigatório") UUID exercicioId,
    @NotBlank(message = "O dia da semana deve ser informado") String diaSemana,
    @NotBlank(message = "O grupo muscular é obrigatório") String grupoMuscular,
    @Min(value = 1, message = "Séries > 0") int series,
    @Min(value = 1, message = "Repetições > 0") int repeticoes,
    String intervalo
) {}