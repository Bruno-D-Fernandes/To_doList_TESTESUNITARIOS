package edu.jUnitEMosquito.exception.usuario;

public class DadoInvalidoException extends RuntimeException {
    public DadoInvalidoException(String dado) {
        super(dado + " recebido inválido.");
    }

}
