package edu.jUnitEMosquito.exception.authorization;

public class UsuarioNaoPossuiPermissao extends RuntimeException {

    public UsuarioNaoPossuiPermissao() {
        super("Usuário não possui permissão para essa ação. Nível necessário: " + roles.name());
    }

    public UsuarioNaoPossuiPermissao(String message) {
        super(message);
    }
}
