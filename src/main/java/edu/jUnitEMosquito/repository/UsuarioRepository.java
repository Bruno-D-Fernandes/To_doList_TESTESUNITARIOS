package edu.jUnitEMosquito.repository;
import edu.jUnitEMosquito.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmail(String email);

    Optional<Usuario> findById(Long newIdOwner);
}
