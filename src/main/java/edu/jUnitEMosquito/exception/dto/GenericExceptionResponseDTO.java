package edu.jUnitEMosquito.exception.dto;

import org.springframework.http.HttpStatus;

public record GenericExceptionResponseDTO(
        HttpStatus httpStatus,
        String message
) {
}
