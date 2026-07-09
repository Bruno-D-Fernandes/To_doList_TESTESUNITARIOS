package edu.jUnitEMosquito.exception.group;

public class UsuarioNaoParticipaDoGrupo extends RuntimeException {
    public UsuarioNaoParticipaDoGrupo() {
        super("Usuário não partipa do grupo.");
    }

    public UsuarioNaoParticipaDoGrupo(String message) {
        super(message);
    }
}
