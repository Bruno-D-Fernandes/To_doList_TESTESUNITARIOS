package edu.jUnitEMosquito.exception.authorization;

import edu.jUnitEMosquito.model.UsuarioGrupo;

public class UsuarioNaoPossuiPermissao extends RuntimeException {

    public UsuarioNaoPossuiPermissao(UsuarioGrupo.Roles roles) {
        super("Usuário não possui permissão para essa ação. Nível necessário: " + roles.name());
    }

    public UsuarioNaoPossuiPermissao(String message) {
        super(message);
    }
}
