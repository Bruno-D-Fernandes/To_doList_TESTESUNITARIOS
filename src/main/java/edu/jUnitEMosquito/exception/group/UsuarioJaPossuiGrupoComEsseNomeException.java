package edu.jUnitEMosquito.exception.group;

public class UsuarioJaPossuiGrupoComEsseNomeException extends RuntimeException {
    public UsuarioJaPossuiGrupoComEsseNomeException() {
        super("Usuário já possui grupo com esse nome");
    }

    public UsuarioJaPossuiGrupoComEsseNomeException(String message) {
        super(message);
    }
}
