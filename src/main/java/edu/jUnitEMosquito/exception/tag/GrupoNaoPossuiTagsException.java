package edu.jUnitEMosquito.exception.tag;

public class GrupoNaoPossuiTagsException extends RuntimeException {
    public GrupoNaoPossuiTagsException() {
        super("O grupo não possui tags.");
    }

    public GrupoNaoPossuiTagsException(String message) {
        super(message);
    }
}
