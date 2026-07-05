package edu.jUnitEMosquito.repository;

import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<List<Group>> findAllByLider(Usuario usuario);

    Optional<List<Group>> findGroupByNomeAndLider(String nome, Usuario user);
}
