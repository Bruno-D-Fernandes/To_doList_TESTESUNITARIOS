package edu.jUnitEMosquito.exception.group;

public class GrupoNaoEncontrado extends RuntimeException {

    public GrupoNaoEncontrado() {
        super("Usuário não participa de nenhum grupo com esse nome.");
    }

    public GrupoNaoEncontrado(String message) {
        super(message);
    }
}
