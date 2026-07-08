package edu.jUnitEMosquito.dto.task;

import java.time.OffsetDateTime;

public record TaskGroupDto(
        Long id,
        String title,
        OffsetDateTime tempoLimite
) {
}
