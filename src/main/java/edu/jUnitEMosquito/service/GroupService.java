package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
import edu.jUnitEMosquito.exception.group.UsuarioJaPossuiGrupoComEsseNomeException;
import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import edu.jUnitEMosquito.repository.GroupRepository;
import edu.jUnitEMosquito.repository.UsuarioGrupoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    private GroupRepository groupRepository;
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UsuarioGrupoRepository usuarioGrupoRepository) {
        this.groupRepository = groupRepository;
        this.usuarioGrupoRepository = usuarioGrupoRepository;
    }

    @Transactional
    public void createGroup(CreateGroupDTO createGroupDTO){

        groupRepository.findGroupByNomeAndLider(createGroupDTO.nomeGrupo(), createGroupDTO.donoGrupo())
                .ifPresent(groups -> new UsuarioJaPossuiGrupoComEsseNomeException());

        Group group = new Group(createGroupDTO.nomeGrupo(), createGroupDTO.donoGrupo());


        //Verificar se o usuário já possuí um grupo com esse nome

        groupRepository.save(group);
    }

    public List<Group> getAllGroupsByAuthUser(Usuario usuarioAuth) {
        Optional<List<Group>> allByUsuario = groupRepository.findAllByLider(usuarioAuth);
        List<Group> groups = allByUsuario.orElseThrow(() -> new RuntimeException("Usuário não possuí grupos!"));

        return groups;
    }

    public void deleteGroup(Usuario usuarioAuth, String groupName){

        // por nome é um péssimo parâmetro para isso, já que um usuário não pode ser dono de 2 grupos com o mesmo nome
        // porem pode participar de mais de um grupo de tasks com o mesmo nome
        UsuarioGrupo usuarioGrupos = usuarioGrupoRepository.findByUsuarioAndGroup_Nome(usuarioAuth, groupName)
                        .orElseThrow(() -> new RuntimeException("Usuário não participa de nenhum grupo com esse nome."));

    }


}
