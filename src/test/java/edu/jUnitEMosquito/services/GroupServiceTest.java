package edu.jUnitEMosquito.services;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
import edu.jUnitEMosquito.exception.group.UsuarioJaPossuiGrupoComEsseNomeException;
import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.repository.GroupRepository;
import edu.jUnitEMosquito.service.GroupService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Autowired
    public GroupServiceTest(GroupService groupService) {
        this.groupService = groupService;
    }

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário tenta criar grupo com nome repetido," +
            " nomes iguais são permitidos para donos diferentes")
    void exceptionThrowCase1(){
        Usuario usuarioMock = new Usuario("Bruno", "bruno@gmail.com", "senha123");
        Group groupRepositoryResponse = new Group("Grupo1", usuarioMock);

        when(groupRepository.findGroupByNomeAndLider("Grupo1", usuarioMock)).thenReturn(Optional.of(List.of(groupRepositoryResponse)));

        Assertions.assertThrows(UsuarioJaPossuiGrupoComEsseNomeException.class, () -> {
            CreateGroupDTO grupoDto = new CreateGroupDTO("Grupo1", usuarioMock);
            groupService.createGroup(grupoDto);
        });

    }



}
