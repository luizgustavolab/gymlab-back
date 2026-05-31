package com.gymlab.api;

import java.util.List;
import com.gymlab.api.ObjetivoTreino;
import com.gymlab.api.Genero;

public record TreinoTemplate(

    ObjetivoTreino objetivo,

    int diasPorSemana,

    Genero genero,

    List<TemplateDia> dias

) {}