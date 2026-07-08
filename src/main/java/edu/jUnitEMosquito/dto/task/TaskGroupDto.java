package edu.jUnitEMosquito.dto.task;

import edu.jUnitEMosquito.model.Task;

import java.time.OffsetDateTime;

public record TaskGroupDto(
        Long id,
        String title,
        OffsetDateTime tempoLimite,
        Task.TaskStatus status
) {
}
