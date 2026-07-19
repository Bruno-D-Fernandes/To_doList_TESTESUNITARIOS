package edu.jUnitEMosquito.services;

import edu.jUnitEMosquito.exception.group.UsuarioNaoParticipaDoGrupo;
import edu.jUnitEMosquito.exception.tag.GrupoNaoPossuiTagsException;
import edu.jUnitEMosquito.exception.tag.TagNaoEncontradaException;
import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Tags;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import edu.jUnitEMosquito.repository.TagsRepository;
import edu.jUnitEMosquito.repository.UsuarioGrupoRepository;
import edu.jUnitEMosquito.service.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagsServiceTest {

    @Mock
    UsuarioGrupoRepository usuarioGrupoRepository;

    @Mock
    TagsRepository tagsRepository;

    @InjectMocks
    TagsService tagsService;

    Usuario usuario;
    Group group;

    @BeforeEach
    void setup() {
        usuario = new Usuario("nome","email","senha");
        usuario.setId(1L);
        group = new Group("g", usuario);
        group.setId(2L);
    }

    @Nested
    class GetTagsByGroupTests {

        @Test
        @DisplayName("retorna tags quando usuário participa e há tags")
        void returnsTagsWhenUserParticipates() {
            Tags tag = new Tags(5L, group, "t");

            when(usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuario, 2L)).thenReturn(true);
            when(tagsRepository.findByGroup_Id(2L)).thenReturn(List.of(tag));

            List<Tags> result = tagsService.getTagsByGroup(usuario, 2L);

            assertEquals(1, result.size());
            assertEquals(tag.getId(), result.get(0).getId());
        }

        @Test
        @DisplayName("lança exceção quando usuário não participa do grupo")
        void throwsWhenUserNotInGroup() {
            when(usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuario, 2L)).thenReturn(false);

            assertThrows(UsuarioNaoParticipaDoGrupo.class, () -> tagsService.getTagsByGroup(usuario, 2L));
        }

        @Test
        @DisplayName("lança exceção quando não há tags no grupo")
        void throwsWhenNoTags() {
            when(usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuario, 2L)).thenReturn(true);
            when(tagsRepository.findByGroup_Id(2L)).thenReturn(List.of());

            assertThrows(GrupoNaoPossuiTagsException.class, () -> tagsService.getTagsByGroup(usuario, 2L));
        }
    }

    @Nested
    class CreateTagTests {

        @Test
        @DisplayName("cria tag quando usuário participa do grupo")
        void createTagSuccess() {
            UsuarioGrupo ug = new UsuarioGrupo(group, usuario, UsuarioGrupo.Roles.MEMBER);
            when(usuarioGrupoRepository.findByUsuarioAndGrupo_Id(usuario, 2L)).thenReturn(Optional.of(ug));

            tagsService.createTag(usuario, 2L, "nova");

            verify(tagsRepository, times(1)).save(any(Tags.class));
        }

        @Test
        @DisplayName("lança exceção quando usuário não participa do grupo")
        void createTagUserNotInGroup() {
            when(usuarioGrupoRepository.findByUsuarioAndGrupo_Id(usuario, 2L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoParticipaDoGrupo.class, () -> tagsService.createTag(usuario, 2L, "x"));
        }
    }

    @Nested
    class DeleteTagTests {

        @Test
        @DisplayName("deleta tag quando usuário participa do grupo")
        void deleteSuccess() {
            when(usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuario, 2L)).thenReturn(true);
            Tags tag = new Tags(30L, group, "t");
            when(tagsRepository.findById(30L)).thenReturn(Optional.of(tag));

            tagsService.deleteTag(usuario, 30L, 2L);

            verify(tagsRepository, times(1)).delete(tag);
        }

        @Test
        @DisplayName("lança exceção quando usuário não participa do grupo")
        void deleteUserNotInGroup() {
            when(usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuario, 2L)).thenReturn(false);

            assertThrows(UsuarioNaoParticipaDoGrupo.class, () -> tagsService.deleteTag(usuario, 1L, 2L));
        }

        @Test
        @DisplayName("lança exceção quando tag não existe")
        void deleteTagNotFound() {
            when(usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuario, 2L)).thenReturn(true);
            when(tagsRepository.findById(40L)).thenReturn(Optional.empty());

            assertThrows(TagNaoEncontradaException.class, () -> tagsService.deleteTag(usuario, 40L, 2L));
        }
    }
}
