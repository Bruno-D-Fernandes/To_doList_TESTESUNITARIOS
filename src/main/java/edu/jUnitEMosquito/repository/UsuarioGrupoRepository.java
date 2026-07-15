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
    Optional<UsuarioGrupo> findByUsuarioAndGrupo_Id(
            Usuario usuario,
            Long id
    );

    @Query("SELECT ug FROM UsuarioGrupo ug " +
            "JOIN FETCH ug.grupo g " +
            "JOIN FETCH ug.usuario u " +
            "WHERE g.id = :id")
    List<UsuarioGrupo> findByGroup_IdN(@Param("id") Long id);


}
