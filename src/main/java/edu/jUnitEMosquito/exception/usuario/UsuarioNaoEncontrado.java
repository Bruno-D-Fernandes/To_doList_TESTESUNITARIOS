package edu.jUnitEMosquito.exception.usuario;

public class UsuarioNaoEncontrado extends RuntimeException {
    public UsuarioNaoEncontrado() {
        super("Usuário não encontrado.");
    }

    public UsuarioNaoEncontrado(String message) {
        super(message);
    }
}
