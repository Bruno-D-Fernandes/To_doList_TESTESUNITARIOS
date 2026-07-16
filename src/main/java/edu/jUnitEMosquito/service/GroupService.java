package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
import edu.jUnitEMosquito.dto.group.MergeGroupDTO;
import edu.jUnitEMosquito.dto.group.ChangeGroupOwnerDTO;
import edu.jUnitEMosquito.dto.group.ChangeGroupNameDTO;
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

import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

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
        if (!matches) throw new NomeDoGrupoInvalido();

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
                                .map(TaskGroupDto::new)
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
    public void changeGroupOwner(Usuario usuarioAuth, ChangeGroupOwnerDTO dto) {
        UsuarioGrupo currentOwner = getUsuarioGrupoAutenticado(usuarioAuth, dto.groupId());
        List<UsuarioGrupo> usuarioGrupoList = getUsuarioGrupoByGroupId(dto.groupId());

        List<UsuarioGrupo> newOwnerList = usuarioGrupoList.stream()
                .filter((usuarioGrupo) -> usuarioGrupo.getUsuario().getId() == dto.newOwnerId())
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

    @Transactional
    public void changeGroupName(Usuario usuarioAuth, ChangeGroupNameDTO dto) {
        UsuarioGrupo owner = getUsuarioGrupoAutenticado(usuarioAuth, dto.groupId());

        if (!owner.getGrupo().getNome().equals(dto.newName())) {
            if (owner.getRoles() == UsuarioGrupo.Roles.MEMBER) 
                throw new UsuarioNaoPossuiPermissao("Membros não podem trocar nome do grupo.");

            List<Group> groupByNomeAndLider = groupRepository.findGroupByNomeAndLider(dto.newName(), owner.getUsuario());
            if (groupByNomeAndLider.size() != 0) 
                throw new UsuarioJaPossuiGrupoComEsseNomeException("Dono do grupo já possui grupo com esse nome.");

            boolean matches = Pattern.matches("^(?=(?:[^ ]* ){0,3}[^ ]*$)[A-Za-z0-9 ]{5,15}$", dto.newName());
            if (!matches) throw new NomeDoGrupoInvalido();

            Group group = owner.getGrupo();
            group.setNome(dto.newName());
            groupRepository.save(group);
        }
    }

    // Pega o UsuarioGrupo do usuário autenticado, se ele não for dono do grupo, lança uma exception
    private UsuarioGrupo getUsuarioGrupoAutenticado(Usuario usuarioAuth, Long groupId) {
        List<UsuarioGrupo> usuarioGrupoList = getUsuarioGrupoByGroupId(groupId);
        return usuarioGrupoList.stream()
                .filter((usuarioGrupo) -> usuarioGrupo.getUsuario() == usuarioAuth)
                .findFirst()
                .orElseThrow(() -> new UsuarioNaoParticipaDoGrupo());
    }

    // Pega todos os UsuarioGrupo de um grupo, se não houver nenhum, lança uma exception
    private List<UsuarioGrupo> getUsuarioGrupoByGroupId(Long groupId) {
        List<UsuarioGrupo> usuarioGrupoList = usuarioGrupoRepository.findByGroup_IdN(groupId);
        if (usuarioGrupoList.size() < 1) throw new GrupoNaoEncontrado();
        return usuarioGrupoList;
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
