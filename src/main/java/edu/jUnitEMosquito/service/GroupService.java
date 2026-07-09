package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
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
    // talvez seja interessante quebrar esse método em 2
    // ou talvez quebrar isso em métodos privados, por se tratar de um projeto pessoal, não sei se compensa o tempo
    public void mergeGroup(
            Usuario usuarioAuth,
            String newName,
            Long newIdOwner,
            Long groupId
    ){
        // Pego todos os membros e roles de um grupo, JoinFetch
        List<UsuarioGrupo> newUsuarioGroup = usuarioGrupoRepository.findByGroup_IdN(groupId);
        if(newUsuarioGroup.size() < 1) throw new GrupoNaoEncontrado();

            List<UsuarioGrupo> ownerList = newUsuarioGroup.stream()
                    .filter((usuarioGrupo) -> usuarioGrupo.getUsuario() == usuarioAuth)
                    .toList();

            List<UsuarioGrupo> newOwnerList = newUsuarioGroup.stream()
                    .filter((usuarioGrupo) -> usuarioGrupo.getUsuario().getId() == newIdOwner)
                    .toList();

            if(newOwnerList.size() != 1) throw new UsuarioNaoParticipaDoGrupo();
            if(ownerList.size() != 1) throw new UsuarioNaoParticipaDoGrupo();

            UsuarioGrupo owner = ownerList.get(0);
            UsuarioGrupo newOwner = newOwnerList.get(0);

            // um usuário não pode receber a posse de um grupo se já possuir um groupo com esse nome
            // Se true significa uma mudança de líder
            if(!owner.equals(newOwner)){
                if(!(owner.getRoles() == UsuarioGrupo.Roles.OWNER)) throw new UsuarioNaoPossuiPermissao(UsuarioGrupo.Roles.OWNER);

                // N + 1 resolvível com groupRepository.findBy + JoinFetch | não faço isso por que penso que ia
                // aumentar muito a complexidade desse método, que já está extenso
                List<Group> groupByNomeAndLider = groupRepository.findGroupByNomeAndLider(owner.getGrupo().getNome(), newOwner.getUsuario());
                if(groupByNomeAndLider.size() != 0) throw new UsuarioJaPossuiGrupoComEsseNomeException();

                owner.setRoles(UsuarioGrupo.Roles.MEMBER);
                newOwner.setRoles(UsuarioGrupo.Roles.OWNER);

                usuarioGrupoRepository.save(owner);
                usuarioGrupoRepository.save(newOwner);
            }

            if(owner.getGrupo().getNome().equals(newName)){
                // Tenho que aprender esse négocio de aspecto
                if(owner.getRoles() == UsuarioGrupo.Roles.MEMBER) throw new UsuarioNaoPossuiPermissao("Membros não podem trocar nome do grupo.");

                // Dono do grupo já possui grupo com esse nome
                List<Group> groupByNomeAndLider = groupRepository.findGroupByNomeAndLider(newName, owner.getUsuario());
                if(groupByNomeAndLider.size() != 0) throw new UsuarioJaPossuiGrupoComEsseNomeException("Dono do grupo já possui grupo com esse nome.");

                // Valida o nome do grupo
                boolean matches = Pattern.matches("^(?=(?:[^ ]* ){0,3}[^ ]*$)[A-Za-z0-9 ]{5,15}$", newName);
                if (!matches) throw new NomeDoGrupoInvalido();

                Group group = owner.getGrupo();
                group.setNome(newName);

                //Penso que não seja necessário, porém estou colocando para ter certeza
                groupRepository.save(group);
            }

    }

    // Fazer testes unitários
    @Transactional
    public void deleteGroup(Usuario usuarioAuth, Long groupId){

        UsuarioGrupo usuarioGrupo = usuarioGrupoRepository.findByUsuarioAndGroup_Id(usuarioAuth, groupId)
                        .orElseThrow(() -> new GrupoNaoEncontrado());

        // Aspect?
        // Authorization
        if (usuarioGrupo.getRoles() != UsuarioGrupo.Roles.OWNER)
            throw new UsuarioNaoPossuiPermissao(UsuarioGrupo.Roles.OWNER);

        groupRepository.delete(usuarioGrupo.getGrupo());
    }



}
