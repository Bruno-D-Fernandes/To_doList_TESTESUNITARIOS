package edu.jUnitEMosquito.exception.tag;

public class TagJaExisteException extends RuntimeException {
    public TagJaExisteException() {
        super("Tag já existe no grupo.");
    }

    public TagJaExisteException(String message) {
        super(message);
    }
}
