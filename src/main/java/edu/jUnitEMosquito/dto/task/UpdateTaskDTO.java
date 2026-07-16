package edu.jUnitEMosquito.dto.task;

import java.time.OffsetDateTime;

public record UpdateTaskDTO(
        Long taskId,
        Long groupId,
        String title,
        OffsetDateTime dataLimite
) {
}
