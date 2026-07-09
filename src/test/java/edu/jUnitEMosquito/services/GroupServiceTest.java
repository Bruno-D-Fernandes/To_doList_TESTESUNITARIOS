package edu.jUnitEMosquito.services;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
import edu.jUnitEMosquito.dto.group.UserGroupsDto;
import edu.jUnitEMosquito.dto.task.TaskGroupDto;
import edu.jUnitEMosquito.exception.authorization.UsuarioNaoPossuiPermissao;
import edu.jUnitEMosquito.exception.group.GrupoNaoEncontrado;
import edu.jUnitEMosquito.exception.group.NomeDoGrupoInvalido;
import edu.jUnitEMosquito.exception.group.UsuarioJaPossuiGrupoComEsseNomeException;
import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Task;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import edu.jUnitEMosquito.repository.GroupRepository;
import edu.jUnitEMosquito.repository.UsuarioGrupoRepository;
import edu.jUnitEMosquito.service.GroupService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    private Usuario usuario;
    private CreateGroupDTO createGrupoDto;
    private Group group;
    private Task task;
    private UsuarioGrupo usuarioGrupo;

    @BeforeEach
    void setup(){
        usuario = new Usuario("Bruno", "bruno@gmail.com", "123");
        createGrupoDto = new CreateGroupDTO("Grupo1", usuario);
        group = new Group("Grupo1", usuario);
        task = new Task("Lavar roupa", OffsetDateTime.now(), usuario, group);
        usuarioGrupo = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.MEMBER);
    }

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @InjectMocks
    private GroupService groupService;

    @Nested
    class CreateGroup{

        @Test
        @DisplayName("Deve realizar a operação de criação do groupo com sucesso!")
        void succesCase1(){

            // esse when não é necessário, já que ele não tem retorno e sua existência não afeta o funcionamento do trecho
            when(groupRepository.findGroupByNomeAndLider(createGrupoDto.nomeGrupo(), createGrupoDto.donoGrupo()))
                    .thenReturn(Optional.empty());

            groupService.createGroup(createGrupoDto);

            verify(groupRepository, times(1)).save(Mockito.any(Group.class));
        }

        @Test
        @DisplayName("Lança erro devido nome inválido")
        void exceptionThrowCase2(){
            CreateGroupDTO groupInvalidName = new CreateGroupDTO("!ubaw ", usuario);

            RuntimeException nomeDoGrupoInvalido = Assertions.assertThrows(NomeDoGrupoInvalido.class, () -> {
                groupService.createGroup(groupInvalidName);
            });

            Assertions.assertEquals("Nome do grupo inválido.", nomeDoGrupoInvalido.getMessage());
        }


        @Test
        @DisplayName("Deve lançar exceção quando usuário tenta criar grupo com nome repetido," +
                " nomes iguais são permitidos para donos diferentes")
        void exceptionThrowCase1(){
            Usuario usuario = new Usuario("Bruno", "bruno@gmail.com", "123");

            when(groupRepository.findGroupByNomeAndLider("Grupo1", usuario)).thenReturn(Optional.of(List.of(group)));

            Assertions.assertThrows(UsuarioJaPossuiGrupoComEsseNomeException.class, () -> {
                groupService.createGroup(createGrupoDto);
            });

        }
    }

    @Nested
    class GetAllGroupsByAuthUser{


        @Test
        @DisplayName("Valida se os DTO estão sendo mapeados corretamente")
        void  exceptionThrowCase1(){
            when(groupRepository.findByUsuarioN(any())).thenAnswer((argument) -> {
                group.setTasks(List.of(task));
                return List.of(group);
            });

            List<UserGroupsDto> allGroupsByAuthUser = groupService.getAllGroupsByAuthUser(usuario);
            UserGroupsDto userGroupsDto = allGroupsByAuthUser.get(0);
            TaskGroupDto taskGroupDto = userGroupsDto.tasks().get(0);
            Assertions.assertAll(
                    () -> Assertions.assertNotNull(allGroupsByAuthUser, "Neste teste a resposta não deve ser null"),
                    () -> Assertions.assertFalse(allGroupsByAuthUser.isEmpty(), "Teste de caso espera ao menos uma List de size 1"),
                    () -> Assertions.assertInstanceOf(UserGroupsDto.class, allGroupsByAuthUser.get(0), "Retorno diferente do esperado"),

                    () -> Assertions.assertEquals("Grupo1", userGroupsDto.nome()),
                    () -> Assertions.assertNotNull(taskGroupDto, "TaksGroupDto não deve ser null"),
                    () -> Assertions.assertEquals("Lavar roupa", taskGroupDto.title())
            );
        }




    }

    @Nested
    class DeleteGroup{

        @Test
        void exceptionThrowCase1(){

            when(usuarioGrupoRepository.findByUsuarioAndGroup_Id(any(), any())).thenReturn(Optional.empty());

            GrupoNaoEncontrado excecao = Assertions.assertThrows(GrupoNaoEncontrado.class, () -> {
                groupService.deleteGroup(any(), any());
            }, "Exceção deve ser lançada");

            Assertions.assertInstanceOf(GrupoNaoEncontrado.class, excecao, "Lançou a exceção errada");
        }

        @Test
        void exceptionThrowCase2(){

            when(usuarioGrupoRepository.findByUsuarioAndGroup_Id(any(), any())).thenReturn(Optional.of(usuarioGrupo));

            UsuarioNaoPossuiPermissao excecao = Assertions.assertThrows(UsuarioNaoPossuiPermissao.class, () -> {
                groupService.deleteGroup(any(), any());
            }, "Exceção deve ser lançada");

            Assertions.assertInstanceOf(UsuarioNaoPossuiPermissao.class, excecao, "Lançou a exceção errada");
        }

        @Test
        void succesCase1(){
            usuarioGrupo.setRoles(UsuarioGrupo.Roles.OWNER);
            when(usuarioGrupoRepository.findByUsuarioAndGroup_Id(any(), any())).thenReturn(Optional.of(usuarioGrupo));

            groupService.deleteGroup(any(), any());

            // aqui eu poderia usar uma ArgumentCaptor
            verify(groupRepository).delete(any());
        }
    }

}
