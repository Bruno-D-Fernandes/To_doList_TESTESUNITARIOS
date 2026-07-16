package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.dto.task.CreateTaskDTO;
import edu.jUnitEMosquito.dto.task.TaskGroupDto;
import edu.jUnitEMosquito.dto.task.UpdateTaskStatusDTO;
import edu.jUnitEMosquito.dto.task.UpdateTaskDTO;
import edu.jUnitEMosquito.exception.authorization.UsuarioNaoPossuiPermissao;
import edu.jUnitEMosquito.exception.group.UsuarioNaoParticipaDoGrupo;
import edu.jUnitEMosquito.exception.task.GrupoNaoPossuiTasksException;
import edu.jUnitEMosquito.exception.task.TaskNaoEncontradaException;
import edu.jUnitEMosquito.model.Task;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import edu.jUnitEMosquito.repository.TaskRepository;
import edu.jUnitEMosquito.repository.UsuarioGrupoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    UsuarioGrupoRepository usuarioGrupoRepository;
    TaskRepository taskRepository;

    @Autowired
    public TaskService(UsuarioGrupoRepository usuarioGrupoRepository, TaskRepository taskRepository) {
        this.usuarioGrupoRepository = usuarioGrupoRepository;
        this.taskRepository = taskRepository;
    }

    public List<TaskGroupDto> getTasksByGroup(Usuario usuarioAuth, Long groupId) {
        boolean usuarioParticipaDoGrupo = usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuarioAuth, groupId);
        if(!usuarioParticipaDoGrupo) throw new UsuarioNaoParticipaDoGrupo();

        List<Task> tasks = taskRepository.findByGrupo_Id(groupId);
        if(tasks.isEmpty()) throw new GrupoNaoPossuiTasksException();

        return tasks.stream().map(TaskGroupDto::new).toList();
    }

    @Transactional
    public void createTask(CreateTaskDTO createTaskDTO, Usuario usuarioAuth) {
        // N + 1 com o grupo
        UsuarioGrupo usuarioParticipaDoGrupo = usuarioGrupoRepository.findByUsuarioAndGrupo_Id(usuarioAuth, createTaskDTO.GroupId())
                .orElseThrow(() -> new UsuarioNaoParticipaDoGrupo());

        Task task = new Task(createTaskDTO.title(), createTaskDTO.dataLimite(), usuarioAuth, usuarioParticipaDoGrupo.getGrupo(), Task.TaskStatus.WORKING);

        taskRepository.save(task);
    }

    @Transactional
    public void updateTask(Usuario usuarioAuth, UpdateTaskDTO updateTaskDTO) {
        UsuarioGrupo usuarioGrupo = usuarioGrupoRepository.findByUsuarioAndGrupo_Id(usuarioAuth, updateTaskDTO.groupId())
                .orElseThrow(() -> new UsuarioNaoParticipaDoGrupo());

        // N + 1 com a task
        Task task = taskRepository.findById(updateTaskDTO.taskId())
                .orElseThrow(() -> new TaskNaoEncontradaException());


        // Somente o criador ou cargos acima de Admin podem atualizar a task
        if(usuarioGrupo.getRoles() == UsuarioGrupo.Roles.MEMBER && task.getCreator() != usuarioAuth)
            throw new UsuarioNaoPossuiPermissao(UsuarioGrupo.Roles.ADMIN);

        task.setTitle(updateTaskDTO.title());
        task.setDataLimite(updateTaskDTO.dataLimite());
    }

    @Transactional
    public void updateTaskStatus(Usuario usuarioAuth, UpdateTaskStatusDTO updateTaskStatusDTO) {
        boolean usuarioParticipaDoGrupo = usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuarioAuth, updateTaskStatusDTO.groupId());
        if(!usuarioParticipaDoGrupo) throw new UsuarioNaoParticipaDoGrupo();

        Task task = taskRepository.findById(updateTaskStatusDTO.taskId())
                .orElseThrow(() -> new TaskNaoEncontradaException());

        task.setTaskStatus(updateTaskStatusDTO.newStatus());
    }

    @Transactional
    public void deleteTask(Usuario usuarioAuth, Long taskId, Long groupId) {
        boolean usuarioParticipaDoGrupo = usuarioGrupoRepository.existsByUsuarioAndGrupo_Id(usuarioAuth, groupId);
        if(!usuarioParticipaDoGrupo) throw new UsuarioNaoParticipaDoGrupo();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNaoEncontradaException());

        taskRepository.delete(task);
    }
}
