package edu.jUnitEMosquito.exception.group;

public class NomeDoGrupoInvalido extends RuntimeException {
    public NomeDoGrupoInvalido() {
        super("Nome do grupo inválido.");
    }

    public NomeDoGrupoInvalido(String message) {
        super(message);
    }
}
