package edu.jUnitEMosquito.exception.task;

public class GrupoNaoPossuiTasksException extends RuntimeException {
    public GrupoNaoPossuiTasksException() {
        super("O grupo não possui tasks.");
    }

    public GrupoNaoPossuiTasksException(String message) {
        super(message);
    }
}
