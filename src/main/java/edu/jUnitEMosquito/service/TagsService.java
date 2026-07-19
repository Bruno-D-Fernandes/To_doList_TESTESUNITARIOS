package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.exception.group.UsuarioNaoParticipaDoGrupo;
import edu.jUnitEMosquito.exception.tag.GrupoNaoPossuiTagsException;
import edu.jUnitEMosquito.exception.tag.TagNaoEncontradaException;
import edu.jUnitEMosquito.model.Tags;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import edu.jUnitEMosquito.repository.TagsRepository;
import edu.jUnitEMosquito.repository.UsuarioGrupoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagsService {

    UsuarioGrupoRepository usuarioGrupoRepository;
    TagsRepository tagsRepository;

    @Autowired
    public TagsService(UsuarioGrupoRepository usuarioGrupoRepository, TagsRepository tagsRepository) {
        this.usuarioGrupoRepository = usuarioGrupoRepository;
        this.tagsRepository = tagsRepository;
    }

    public List<Tags> getTagsByGroup(Usuario usuarioAuth, Long groupId) {
        boolean usuarioParticipaDoGrupo = usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuarioAuth, groupId);
        if(!usuarioParticipaDoGrupo) throw new UsuarioNaoParticipaDoGrupo();

        List<Tags> tags = tagsRepository.findByGroup_Id(groupId);
        if(tags.isEmpty()) throw new GrupoNaoPossuiTagsException();

        return tags;
    }

    @Transactional
    public void createTag(Usuario usuarioAuth, Long groupId, String name) {
        UsuarioGrupo usuarioParticipaDoGrupo = usuarioGrupoRepository.findByUsuarioAndGrupo_Id(usuarioAuth, groupId)
                .orElseThrow(() -> new UsuarioNaoParticipaDoGrupo());

        Tags tag = new Tags(null, usuarioParticipaDoGrupo.getGrupo(), name);

        tagsRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Usuario usuarioAuth, Long tagId, Long groupId) {
        boolean usuarioParticipaDoGrupo = usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuarioAuth, groupId);
        if(!usuarioParticipaDoGrupo) throw new UsuarioNaoParticipaDoGrupo();

        Tags tag = tagsRepository.findById(tagId)
                .orElseThrow(() -> new TagNaoEncontradaException());

        tagsRepository.delete(tag);
    }

}
