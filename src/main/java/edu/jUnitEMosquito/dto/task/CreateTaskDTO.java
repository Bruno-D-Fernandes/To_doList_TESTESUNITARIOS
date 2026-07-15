package edu.jUnitEMosquito.dto.task;

import java.time.Instant;
import java.time.OffsetDateTime;

public record CreateTaskDTO(
        String title,
        OffsetDateTime dataLimite,
        Long GroupId
) {
}
