package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.dto.task.CreateTaskDTO;
import edu.jUnitEMosquito.exception.group.UsuarioNaoParticipaDoGrupo;
import edu.jUnitEMosquito.model.Task;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.model.UsuarioGrupo;
import edu.jUnitEMosquito.repository.TaskRepository;
import edu.jUnitEMosquito.repository.UsuarioGrupoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Transactional
    public void createTask(CreateTaskDTO createTaskDTO, Usuario usuarioAuth) {
        UsuarioGrupo usuarioParticipaDoGrupo = usuarioGrupoRepository.findByUsuarioAndGrupo_Id(usuarioAuth, createTaskDTO.GroupId())
                .orElseThrow(() -> new UsuarioNaoParticipaDoGrupo());

        Task task = new Task(createTaskDTO.title(), createTaskDTO.dataLimite(), usuarioAuth, usuarioParticipaDoGrupo.getGrupo(), Task.TaskStatus.WORKING);

        taskRepository.save(task);
    }
}
