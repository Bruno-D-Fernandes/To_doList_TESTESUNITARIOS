package edu.jUnitEMosquito.dto.tags;

import edu.jUnitEMosquito.model.Tags;

public record TagDto(
        Long id,
        String name,
        Long groupId
) {
    public TagDto(Tags tag) {
        this(tag.getId(), tag.getName(), tag.getGroup().getId());
    }
}
