package edu.jUnitEMosquito.dto.task;

import edu.jUnitEMosquito.model.Task;

public record UpdateTaskStatusDTO(
        Long taskId,
        Long groupId,
        Task.TaskStatus newStatus
) {
}
