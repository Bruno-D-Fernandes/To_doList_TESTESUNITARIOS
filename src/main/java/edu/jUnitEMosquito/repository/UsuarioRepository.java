package edu.jUnitEMosquito.repository;
import edu.jUnitEMosquito.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsuarioRepository {

    private List<Usuario> repo = new ArrayList<Usuario>();

    public void save(Usuario usuario){
        repo.add(usuario);
    }

    public void remove(Usuario usuario){
        repo.remove(usuario);
    }

    public Usuario getByUsername(String nomeUsuario){
        return repo.stream()
                .filter(user -> user.getUsername().equals(nomeUsuario))
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
    }


}
