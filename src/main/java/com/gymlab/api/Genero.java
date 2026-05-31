package com.gymlab.api;

public enum Genero {

    MASCULINO(1),
    FEMININO(2);

    private final int codigo;

    Genero(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}