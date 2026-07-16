package edu.jUnitEMosquito.exception.task;

public class TaskNaoEncontradaException extends RuntimeException {
    public TaskNaoEncontradaException() {
        super("Task não encontrada.");
    }

    public TaskNaoEncontradaException(String message) {
        super(message);
    }
}
