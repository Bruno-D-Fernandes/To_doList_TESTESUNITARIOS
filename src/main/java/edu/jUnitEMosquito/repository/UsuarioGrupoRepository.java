package edu.jUnitEMosquito.repository;

import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioGrupoRepository extends JpaRepository<UsuarioGrupo, Long> {
    Optional<UsuarioGrupo> findByUsuarioAndGroup_Nome(
            Usuario usuario,
            String nome
    );
}
