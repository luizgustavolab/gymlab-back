package com.gymlab.api;

import com.gymlab.api.Genero;
import com.gymlab.api.ObjetivoTreino;
import org.springframework.stereotype.Component;

@Component
public class TemplateResolver {

    private final HipertrofiaTemplateProvider provider;

    public TemplateResolver(HipertrofiaTemplateProvider provider) {
        this.provider = provider;
    }

    public TreinoTemplate resolve(ObjetivoTreino objetivo, Genero genero, int dias) {
        return provider.build(objetivo, genero, dias);
    }
}