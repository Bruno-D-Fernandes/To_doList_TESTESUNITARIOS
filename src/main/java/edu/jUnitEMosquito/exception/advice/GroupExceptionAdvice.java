package edu.jUnitEMosquito.exception.advice;

import edu.jUnitEMosquito.exception.dto.GenericExceptionResponseDTO;
import edu.jUnitEMosquito.exception.group.UsuarioJaPossuiGrupoComEsseNomeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GroupExceptionAdvice {

    @ExceptionHandler(UsuarioJaPossuiGrupoComEsseNomeException.class)
    public ResponseEntity usuarioJaPossuiGrupoComEsseNomeException(RuntimeException exception){
        GenericExceptionResponseDTO response = new GenericExceptionResponseDTO(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.ok(response);
    }

}
