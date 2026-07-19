package edu.jUnitEMosquito.controller;

import edu.jUnitEMosquito.dto.tags.CreateTagDTO;
import edu.jUnitEMosquito.dto.tags.TagDto;
import edu.jUnitEMosquito.model.Tags;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tags")
public class TagsController {

    private final TagsService tagsService;

    @Autowired
    public TagsController(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity getTagsByGroup(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @PathVariable Long groupId
    ) {
        List<Tags> tags = tagsService.getTagsByGroup(usuarioAuth, groupId);
        List<TagDto> dtos = tags.stream().map(TagDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of(groupId, dtos));
    }

    @PostMapping("/create")
    public ResponseEntity createTag(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @RequestBody CreateTagDTO createTagDTO
    ) {
        tagsService.createTag(usuarioAuth, createTagDTO.groupId(), createTagDTO.name());
        return ResponseEntity.ok("Tag criada com sucesso!");
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity deleteTag(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @PathVariable Long tagId,
            @RequestParam Long groupId
    ) {
        tagsService.deleteTag(usuarioAuth, tagId, groupId);
        return ResponseEntity.ok("Tag deletada com sucesso!");
    }
}
