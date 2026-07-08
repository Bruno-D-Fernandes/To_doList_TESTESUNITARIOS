package edu.jUnitEMosquito.services;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
import edu.jUnitEMosquito.exception.group.UsuarioJaPossuiGrupoComEsseNomeException;
import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.repository.GroupRepository;
import edu.jUnitEMosquito.service.GroupService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Nested
    class CreateGroup{

        private Usuario usuario;
        private CreateGroupDTO createGrupoDto;
        private Group group;

        @BeforeEach
        void setup(){
            usuario = new Usuario("Bruno", "bruno@gmail.com", "123");
            createGrupoDto = new CreateGroupDTO("Grupo1", usuario);
            group = new Group("Grupo1", usuario);
        }

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




}
