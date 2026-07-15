package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
import edu.jUnitEMosquito.dto.group.MergeGroupDTO;
import edu.jUnitEMosquito.dto.group.UserGroupsDto;
import edu.jUnitEMosquito.dto.task.TaskGroupDto;
import edu.jUnitEMosquito.exception.group.GrupoNaoEncontrado;
import edu.jUnitEMosquito.exception.group.NomeDoGrupoInvalido;
import edu.jUnitEMosquito.exception.group.UsuarioJaPossuiGrupoComEsseNomeException;
import edu.jUnitEMosquito.exception.authorization.UsuarioNaoPossuiPermissao;
import edu.jUnitEMosquito.exception.group.UsuarioNaoParticipaDoGrupo;
import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import edu.jUnitEMosquito.repository.GroupRepository;
import edu.jUnitEMosquito.repository.UsuarioGrupoRepository;
import edu.jUnitEMosquito.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class GroupService {

    private GroupRepository groupRepository;
    private UsuarioGrupoRepository usuarioGrupoRepository;
    private UsuarioRepository usuarioRepository;


    @Autowired
    public GroupService(GroupRepository groupRepository, UsuarioGrupoRepository usuarioGrupoRepository, UsuarioRepository usuarioRepository) {
        this.groupRepository = groupRepository;
        this.usuarioGrupoRepository = usuarioGrupoRepository;
        this.usuarioRepository = usuarioRepository;
    }


    @Transactional
    public void createGroup(CreateGroupDTO createGroupDTO){

        // De os seus pulos pra lembrar desse regex ae Bruno de futuro
        // Valida o nome do grupo
        boolean matches = Pattern.matches("^(?=(?:[^ ]* ){0,3}[^ ]*$)[A-Za-z0-9 ]{5,15}$", createGroupDTO.nomeGrupo());
        if (matches) throw new NomeDoGrupoInvalido();

        // Verifica se o usuário já possui um grupo com esse nome
        List<Group> groupByNomeAndLider = groupRepository.findGroupByNomeAndLider(createGroupDTO.nomeGrupo(), createGroupDTO.donoGrupo());
        if(groupByNomeAndLider.size() != 0) throw new UsuarioJaPossuiGrupoComEsseNomeException();

        Group group = new Group(createGroupDTO.nomeGrupo(), createGroupDTO.donoGrupo());

        groupRepository.save(group);
    }

    public List<UserGroupsDto> getAllGroupsByAuthUser(Usuario usuarioAuth) {

        // Um usuário não ter grupos é um erro?
        // não
        List<Group> groupList = groupRepository.findByUsuarioN(usuarioAuth);
        List<UserGroupsDto> response = null;
        if(groupList != null){
             response = groupList.stream()
                    .map(userGroup -> {
                        List<TaskGroupDto> taskGroupDto = userGroup.getTasks().stream()
                                .map(task -> new TaskGroupDto(
                                        task.getId(),
                                        task.getTitle(),
                                        task.getDataLimite(),
                                        task.getTaskStatus()
                                ))
                                .toList();

                        return new UserGroupsDto(
                                userGroup.getId(),
                                userGroup.getNome(),
                                taskGroupDto
                        );
                    })
                    .toList();
        }


        return response;
    }


    @Transactional
    public void mergeGroup(Usuario usuarioAuth, MergeGroupDTO mergeGroupDTO) {
        List<UsuarioGrupo> newUsuarioGroup = usuarioGrupoRepository.findByGroup_IdN(mergeGroupDTO.groupId());
        if (newUsuarioGroup.size() < 1) throw new GrupoNaoEncontrado();

        UsuarioGrupo owner = newUsuarioGroup.stream()
                .filter((usuarioGrupo) -> usuarioGrupo.getUsuario() == usuarioAuth)
                .findFirst()
                .orElseThrow(() -> new UsuarioNaoParticipaDoGrupo());

        changeGroupOwner(newUsuarioGroup, owner, mergeGroupDTO.newIdOwner());
        changeGroupName(owner, mergeGroupDTO.newName());
    }

    private void changeGroupOwner(List<UsuarioGrupo> usuarioGrupoList, UsuarioGrupo currentOwner, Long newOwnerId) {
        List<UsuarioGrupo> newOwnerList = usuarioGrupoList.stream()
                .filter((usuarioGrupo) -> usuarioGrupo.getUsuario().getId() == newOwnerId)
                .toList();

        if (newOwnerList.size() != 1) throw new UsuarioNaoParticipaDoGrupo();

        UsuarioGrupo newOwner = newOwnerList.get(0);

        if (!currentOwner.equals(newOwner)) {
            if (currentOwner.getRoles() != UsuarioGrupo.Roles.OWNER) 
                throw new UsuarioNaoPossuiPermissao(UsuarioGrupo.Roles.OWNER);

            List<Group> groupByNomeAndLider = groupRepository.findGroupByNomeAndLider(
                    currentOwner.getGrupo().getNome(), newOwner.getUsuario());
            if (groupByNomeAndLider.size() != 0) 
                throw new UsuarioJaPossuiGrupoComEsseNomeException();

            currentOwner.setRoles(UsuarioGrupo.Roles.MEMBER);
            newOwner.setRoles(UsuarioGrupo.Roles.OWNER);

            usuarioGrupoRepository.save(currentOwner);
            usuarioGrupoRepository.save(newOwner);
        }
    }

    private void changeGroupName(UsuarioGrupo owner, String newName) {
        if (!owner.getGrupo().getNome().equals(newName)) {
            if (owner.getRoles() == UsuarioGrupo.Roles.MEMBER) 
                throw new UsuarioNaoPossuiPermissao("Membros não podem trocar nome do grupo.");

            List<Group> groupByNomeAndLider = groupRepository.findGroupByNomeAndLider(newName, owner.getUsuario());
            if (groupByNomeAndLider.size() != 0) 
                throw new UsuarioJaPossuiGrupoComEsseNomeException("Dono do grupo já possui grupo com esse nome.");

            boolean matches = Pattern.matches("^(?=(?:[^ ]* ){0,3}[^ ]*$)[A-Za-z0-9 ]{5,15}$", newName);
            if (!matches) throw new NomeDoGrupoInvalido();

            Group group = owner.getGrupo();
            group.setNome(newName);
            groupRepository.save(group);
        }
    }

    // Fazer testes unitários
    @Transactional
    public void deleteGroup(Usuario usuarioAuth, Long groupId){

        UsuarioGrupo usuarioGrupo = usuarioGrupoRepository.findByUsuarioAndGrupo_Id(usuarioAuth, groupId)
                        .orElseThrow(() -> new GrupoNaoEncontrado());

        // Aspect?
        // Authorization
        if (usuarioGrupo.getRoles() != UsuarioGrupo.Roles.OWNER)
            throw new UsuarioNaoPossuiPermissao(UsuarioGrupo.Roles.OWNER);

        groupRepository.delete(usuarioGrupo.getGrupo());
    }



}
