package edu.jUnitEMosquito.dto.group;

import edu.jUnitEMosquito.dto.task.TaskGroupDto;

import java.util.List;

public record UserGroupsDto(
        Long id,
        String nome,
        List<TaskGroupDto> tasks
) {
}
