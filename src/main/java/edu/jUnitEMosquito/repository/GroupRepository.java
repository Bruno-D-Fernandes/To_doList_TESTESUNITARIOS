package edu.jUnitEMosquito.repository;

import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    // Má prática
    Optional<List<Group>> findAllByLider(Usuario usuario);

    List<Group> findGroupByNomeAndLider(String nome, Usuario user);

    @Query(
            "SELECT g FROM Group g " +
                    "JOIN FETCH g.tasks " +
                    "JOIN g.usuarioGrupos ug " +
                    "WHERE ug.usuario = :usuario"
    )
    List<Group> findByUsuarioN(@Param("usuario") Usuario usuario);
}
