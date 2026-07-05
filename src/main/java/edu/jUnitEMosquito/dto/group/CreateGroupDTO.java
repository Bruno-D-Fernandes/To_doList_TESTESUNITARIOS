package edu.jUnitEMosquito.dto.group;

import edu.jUnitEMosquito.model.Usuario;

public record CreateGroupDTO(
        String nomeGrupo,
        Usuario donoGrupo
) {
}
