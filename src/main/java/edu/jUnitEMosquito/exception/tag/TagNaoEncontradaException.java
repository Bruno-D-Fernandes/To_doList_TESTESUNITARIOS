package edu.jUnitEMosquito.exception.tag;

public class TagNaoEncontradaException extends RuntimeException {
    public TagNaoEncontradaException() {
        super("Tag não encontrada.");
    }

    public TagNaoEncontradaException(String message) {
        super(message);
    }
}
