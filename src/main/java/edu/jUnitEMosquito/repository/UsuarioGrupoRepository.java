package edu.jUnitEMosquito.repository;

import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioGrupoRepository extends JpaRepository<UsuarioGrupo, Long> {
    Optional<UsuarioGrupo> findByUsuarioAndGroup_Id(
            Usuario usuario,
            Long id
    );

    @Query(
            "SELECT ug FROM UsuarioGrupo ug " +
            "JOIN FETCH ug.usuario " +
            "JOIN FETCH ug.grupo " +
            "WHERE ug.usuario = :usuario"
    )
    Optional<List<UsuarioGrupo>> findByUsuarioN(@Param("usuario") Usuario usuario);
}
