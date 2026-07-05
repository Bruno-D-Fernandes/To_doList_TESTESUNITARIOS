package edu.jUnitEMosquito.controller;

import edu.jUnitEMosquito.dto.group.CreateGroupDTO;
import edu.jUnitEMosquito.model.Group;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.repository.UsuarioGrupoRepository;
import edu.jUnitEMosquito.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("grupo")
public class GroupController {

    private GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/criar")
    public ResponseEntity criarGrupo(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @RequestBody String nomeGrupo
            ){

        CreateGroupDTO createGroupDTO = new CreateGroupDTO(nomeGrupo, usuarioAuth);
        groupService.createGroup(createGroupDTO);

        return ResponseEntity.ok("Grupo criado com sucesso!");
    }

    @GetMapping("/getMines")
    public ResponseEntity getAllGroupsByAuthUser(
            @AuthenticationPrincipal Usuario usuarioAuth
    ){
        List<Group> groupList = groupService.getAllGroupsByAuthUser(usuarioAuth);

        return ResponseEntity.ok().build();
    }

    // resolver lógica de négocio na camada de service
    @DeleteMapping("/DeleteGroup")
    public ResponseEntity deleteGroup(){
        return ResponseEntity.ok().build();
    }



}
