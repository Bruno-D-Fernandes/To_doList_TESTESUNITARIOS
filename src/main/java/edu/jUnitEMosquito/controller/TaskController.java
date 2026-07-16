package edu.jUnitEMosquito.controller;

import edu.jUnitEMosquito.dto.task.CreateTaskDTO;
import edu.jUnitEMosquito.dto.task.TaskGroupDto;
import edu.jUnitEMosquito.dto.task.UpdateTaskStatusDTO;
import edu.jUnitEMosquito.dto.task.UpdateTaskDTO;
import edu.jUnitEMosquito.model.Task;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping("/group/{groupId}")
    public ResponseEntity getTasksByGroup(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @PathVariable Long groupId
    ) {
        List<TaskGroupDto> tasksByGroup = taskService.getTasksByGroup(usuarioAuth, groupId);
        return ResponseEntity.ok(Map.of(groupId, tasksByGroup));
    }

    @PostMapping("/create")
    public ResponseEntity createTask(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @RequestBody CreateTaskDTO createTaskDTO
            ) {
        taskService.createTask(createTaskDTO, usuarioAuth);
        return ResponseEntity.ok("Task criada com sucesso!");
    }

    @PutMapping("/update-status")
    public ResponseEntity updateTaskStatus(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @RequestBody UpdateTaskStatusDTO updateTaskStatusDTO
    ) {
        taskService.updateTaskStatus(usuarioAuth, updateTaskStatusDTO);
        return ResponseEntity.ok("Task status atualizado com sucesso!");
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity deleteTask(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @PathVariable Long taskId,
            @RequestParam Long groupId
    ) {
        taskService.deleteTask(usuarioAuth, taskId, groupId);
        return ResponseEntity.ok("Task deletada com sucesso!");
    }

    @PutMapping("/update")
    public ResponseEntity updateTask(
            @AuthenticationPrincipal Usuario usuarioAuth,
            @RequestBody UpdateTaskDTO updateTaskDTO
    ) {
        taskService.updateTask(usuarioAuth, updateTaskDTO);
        return ResponseEntity.ok("Task atualizada com sucesso!");
    }
}
