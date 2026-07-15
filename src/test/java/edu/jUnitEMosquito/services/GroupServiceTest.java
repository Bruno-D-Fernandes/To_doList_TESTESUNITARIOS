package edu.jUnitEMosquito.services;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
import edu.jUnitEMosquito.dto.group.MergeGroupDTO;
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
                    .thenReturn(List.of());

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

            when(groupRepository.findGroupByNomeAndLider("Grupo1", usuario)).thenReturn(List.of(group));

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

            when(usuarioGrupoRepository.findByUsuarioAndGrupo_Id(any(), any())).thenReturn(Optional.empty());

            GrupoNaoEncontrado excecao = Assertions.assertThrows(GrupoNaoEncontrado.class, () -> {
                groupService.deleteGroup(any(), any());
            }, "Exceção deve ser lançada");

            Assertions.assertInstanceOf(GrupoNaoEncontrado.class, excecao, "Lançou a exceção errada");
        }

        @Test
        void exceptionThrowCase2(){

            when(usuarioGrupoRepository.findByUsuarioAndGrupo_Id(any(), any())).thenReturn(Optional.of(usuarioGrupo));

            UsuarioNaoPossuiPermissao excecao = Assertions.assertThrows(UsuarioNaoPossuiPermissao.class, () -> {
                groupService.deleteGroup(any(), any());
            }, "Exceção deve ser lançada");

            Assertions.assertInstanceOf(UsuarioNaoPossuiPermissao.class, excecao, "Lançou a exceção errada");
        }

        @Test
        void succesCase1(){
            usuarioGrupo.setRoles(UsuarioGrupo.Roles.OWNER);
            when(usuarioGrupoRepository.findByUsuarioAndGrupo_Id(any(), any())).thenReturn(Optional.of(usuarioGrupo));

            groupService.deleteGroup(any(), any());

            // aqui eu poderia usar uma ArgumentCaptor
            verify(groupRepository).delete(any());
        }
    }

    @Nested
    class MergeGroup{

        private MergeGroupDTO mergeGroupDTOCorrect;
        private MergeGroupDTO mergeGroupDTOException;
        private Usuario novoOwner;

        @BeforeEach
        void setup(){
            novoOwner = new Usuario("Pedro", "pedro@gmail.com", "456");
            mergeGroupDTOCorrect = new MergeGroupDTO("Novo Grupo", 2L, 1L);
            mergeGroupDTOException = new MergeGroupDTO("Grupo Inválido", 3L, 1L);
        }

        @Nested
        class ChangeGroupOwner{

            @Test
            @DisplayName("Deve lançar GrupoNaoEncontrado quando grupo não existe")
            void exceptionThrowCase1(){
                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of());

                GrupoNaoEncontrado excecao = Assertions.assertThrows(GrupoNaoEncontrado.class, () -> {
                    groupService.mergeGroup(usuario, mergeGroupDTOCorrect);
                });

                Assertions.assertInstanceOf(GrupoNaoEncontrado.class, excecao);
            }

            @Test
            @DisplayName("Deve lançar UsuarioNaoParticipaDoGrupo quando usuário autenticado não participa do grupo")
            void exceptionThrowCase2(){
                Usuario usuarioNaoParticipaDoGrupo = new Usuario("João", "joao@gmail.com", "789");
                UsuarioGrupo usuarioGrupoNaoAuth = new UsuarioGrupo(group, novoOwner, UsuarioGrupo.Roles.OWNER);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioGrupoNaoAuth));

                UsuarioNaoParticipaDoGrupo excecao = Assertions.assertThrows(UsuarioNaoParticipaDoGrupo.class, () -> {
                    groupService.mergeGroup(usuarioNaoParticipaDoGrupo, mergeGroupDTOCorrect);
                });

                Assertions.assertInstanceOf(UsuarioNaoParticipaDoGrupo.class, excecao);
            }

            @Test
            @DisplayName("Deve lançar UsuarioNaoParticipaDoGrupo quando novo proprietário não participa do grupo")
            void exceptionThrowCase3(){
                UsuarioGrupo usuarioOwner = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.OWNER);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioOwner));

                UsuarioNaoParticipaDoGrupo excecao = Assertions.assertThrows(UsuarioNaoParticipaDoGrupo.class, () -> {
                    groupService.mergeGroup(usuario, mergeGroupDTOCorrect);
                });

                Assertions.assertInstanceOf(UsuarioNaoParticipaDoGrupo.class, excecao);
            }

            @Test
            @DisplayName("Deve lançar UsuarioNaoPossuiPermissao quando dono atual não possui role OWNER")
            void exceptionThrowCase4(){
                UsuarioGrupo usuarioMember = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.MEMBER);
                UsuarioGrupo usuarioNovoOwner = new UsuarioGrupo(group, novoOwner, UsuarioGrupo.Roles.MEMBER);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioMember, usuarioNovoOwner));

                UsuarioNaoPossuiPermissao excecao = Assertions.assertThrows(UsuarioNaoPossuiPermissao.class, () -> {
                    groupService.mergeGroup(usuario, mergeGroupDTOCorrect);
                });

                Assertions.assertInstanceOf(UsuarioNaoPossuiPermissao.class, excecao);
            }

            @Test
            @DisplayName("Deve lançar UsuarioJaPossuiGrupoComEsseNomeException quando novo owner já possui grupo com mesmo nome")
            void exceptionThrowCase5(){
                UsuarioGrupo usuarioOwner = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.OWNER);
                UsuarioGrupo usuarioNovoOwner = new UsuarioGrupo(group, novoOwner, UsuarioGrupo.Roles.MEMBER);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioOwner, usuarioNovoOwner));
                when(groupRepository.findGroupByNomeAndLider(group.getNome(), novoOwner)).thenReturn(List.of(group));

                UsuarioJaPossuiGrupoComEsseNomeException excecao = Assertions.assertThrows(UsuarioJaPossuiGrupoComEsseNomeException.class, () -> {
                    groupService.mergeGroup(usuario, mergeGroupDTOCorrect);
                });

                Assertions.assertInstanceOf(UsuarioJaPossuiGrupoComEsseNomeException.class, excecao);
            }

            @Test
            @DisplayName("Deve transferir a posse do grupo com sucesso")
            void successCase1(){
                UsuarioGrupo usuarioOwner = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.OWNER);
                UsuarioGrupo usuarioNovoOwner = new UsuarioGrupo(group, novoOwner, UsuarioGrupo.Roles.MEMBER);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioOwner, usuarioNovoOwner));
                when(groupRepository.findGroupByNomeAndLider(group.getNome(), novoOwner)).thenReturn(List.of());

                groupService.mergeGroup(usuario, mergeGroupDTOCorrect);

                Assertions.assertAll(
                        () -> Assertions.assertEquals(UsuarioGrupo.Roles.MEMBER, usuarioOwner.getRoles(), "Dono anterior deve ser degradado para MEMBER"),
                        () -> Assertions.assertEquals(UsuarioGrupo.Roles.OWNER, usuarioNovoOwner.getRoles(), "Novo dono deve ser promovido para OWNER"),
                        () -> verify(usuarioGrupoRepository, times(2)).save(any(UsuarioGrupo.class))
                );
            }

            @Test
            @DisplayName("Não deve fazer nada quando novo owner é igual ao atual")
            void successCase2(){
                UsuarioGrupo usuarioOwner = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.OWNER);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioOwner));

                groupService.mergeGroup(usuario, new MergeGroupDTO("Novo Grupo", usuario.getId(), 1L));

                verify(usuarioGrupoRepository, never()).save(any(UsuarioGrupo.class));
            }
        }

        @Nested
        class ChangeGroupName{

            @Test
            @DisplayName("Deve lançar UsuarioNaoPossuiPermissao quando membro tenta alterar nome do grupo")
            void exceptionThrowCase1(){
                UsuarioGrupo usuarioMember = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.MEMBER);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioMember));

                UsuarioNaoPossuiPermissao excecao = Assertions.assertThrows(UsuarioNaoPossuiPermissao.class, () -> {
                    groupService.mergeGroup(usuario, mergeGroupDTOCorrect);
                });

                Assertions.assertInstanceOf(UsuarioNaoPossuiPermissao.class, excecao);
                Assertions.assertTrue(excecao.getMessage().contains("Membros não podem trocar nome do grupo"));
            }

            @Test
            @DisplayName("Deve lançar NomeDoGrupoInvalido quando nome não segue o padrão")
            void exceptionThrowCase2(){
                UsuarioGrupo usuarioOwner = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.OWNER);
                MergeGroupDTO mergeInvalidName = new MergeGroupDTO("!@#$%", usuario.getId(), 1L);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioOwner));

                NomeDoGrupoInvalido excecao = Assertions.assertThrows(NomeDoGrupoInvalido.class, () -> {
                    groupService.mergeGroup(usuario, mergeInvalidName);
                });

                Assertions.assertInstanceOf(NomeDoGrupoInvalido.class, excecao);
            }

            @Test
            @DisplayName("Deve lançar UsuarioJaPossuiGrupoComEsseNomeException quando dono já possui grupo com novo nome")
            void exceptionThrowCase3(){
                UsuarioGrupo usuarioOwner = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.OWNER);
                MergeGroupDTO mergeNewName = new MergeGroupDTO("Novo Grupo", usuario.getId(), 1L);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioOwner));
                when(groupRepository.findGroupByNomeAndLider("Novo Grupo", usuario)).thenReturn(List.of(new Group("Novo Grupo", usuario)));

                UsuarioJaPossuiGrupoComEsseNomeException excecao = Assertions.assertThrows(UsuarioJaPossuiGrupoComEsseNomeException.class, () -> {
                    groupService.mergeGroup(usuario, mergeNewName);
                });

                Assertions.assertInstanceOf(UsuarioJaPossuiGrupoComEsseNomeException.class, excecao);
                Assertions.assertTrue(excecao.getMessage().contains("Dono do grupo já possui grupo com esse nome"));
            }

            @Test
            @DisplayName("Deve alterar o nome do grupo com sucesso")
            void successCase1(){
                UsuarioGrupo usuarioOwner = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.OWNER);
                MergeGroupDTO mergeNewName = new MergeGroupDTO("Novo Nome", usuario.getId(), 1L);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioOwner));
                when(groupRepository.findGroupByNomeAndLider("Novo Nome", usuario)).thenReturn(List.of());

                groupService.mergeGroup(usuario, mergeNewName);

                Assertions.assertEquals("Novo Nome", group.getNome(), "Nome do grupo deve ser atualizado");
                verify(groupRepository, times(1)).save(any(Group.class));
            }

            @Test
            @DisplayName("Não deve fazer nada quando novo nome é igual ao atual")
            void successCase2(){
                UsuarioGrupo usuarioOwner = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.OWNER);
                MergeGroupDTO mergeSameName = new MergeGroupDTO("Grupo1", usuario.getId(), 1L);

                when(usuarioGrupoRepository.findByGroup_IdN(1L)).thenReturn(List.of(usuarioOwner));

                groupService.mergeGroup(usuario, mergeSameName);

                verify(groupRepository, never()).save(any(Group.class));
            }
        }
    }
}