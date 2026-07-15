package edu.jUnitEMosquito.controller;

import edu.jUnitEMosquito.dto.task.CreateTaskDTO;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity createTask(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @RequestBody CreateTaskDTO createTaskDTO
            ) {

        taskService.createTask(createTaskDTO, usuarioAuth);

        return ResponseEntity.ok("Task criada com sucesso!");
    }
}
