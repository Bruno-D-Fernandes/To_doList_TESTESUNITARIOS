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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // De os seus pulos pra lembrar desse regex ae Bruno de futuro
        boolean matches = Pattern.matches(createGroupDTO.nomeGrupo(), "^(?=(?:[^ ]* ){0,3}[^ ]*$)[A-Za-z0-9 ]{5,15}$");

        // Fazer tratamento próprio
        if (!matches) throw new RuntimeException("Nome do grupo inválido");


        //Verifica se o usuário já possui um grupo com esse nome
        groupRepository.findGroupByNomeAndLider(createGroupDTO.nomeGrupo(), createGroupDTO.donoGrupo())
                .ifPresent(groups -> {
                    throw new UsuarioJaPossuiGrupoComEsseNomeException();
                });

        

        Group group = new Group(createGroupDTO.nomeGrupo(), createGroupDTO.donoGrupo());


        groupRepository.save(group);
    }

    public List<Group> getAllGroupsByAuthUser(Usuario usuarioAuth) {
        Optional<List<Group>> allByUsuario = groupRepository.findAllByLider(usuarioAuth);
        List<Group> groups = allByUsuario.orElseThrow(() -> new RuntimeException("Usuário não possui grupos!"));

        // colocar Dto's
        return groups;
    }

    // Fazer testes unitários
    @Transactional
    public void deleteGroup(Usuario usuarioAuth, Long groupId){

        UsuarioGrupo usuarioGrupos = usuarioGrupoRepository.findByUsuarioAndGroup_Id(usuarioAuth, groupId)
                // Fazer tratamento de exceção
                        .orElseThrow(() -> new RuntimeException("Usuário não participa de nenhum grupo com esse nome."));

        // Fazer tratamento de exceção
        // Authorization
        if (usuarioGrupos.getRoles() != UsuarioGrupo.Roles.OWNER) throw new RuntimeException("Usuário não tem permissão para essa ação.");

        groupRepository.delete(usuarioGrupos.getGroup());
    }


}
