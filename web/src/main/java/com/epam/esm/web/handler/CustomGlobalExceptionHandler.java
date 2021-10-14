package com.epam.esm.web.handler;

import com.epam.esm.service.exception.EntityExistsException;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.service.exception.InvalidEntityException;
import com.epam.esm.service.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomGlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return getResponseEntityFromErrors(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return getResponseEntityFromErrors(errors);
    }

    @ExceptionHandler({JsonPatchException.class, JsonProcessingException.class})
    protected ResponseEntity<ErrorResponse> handleConflict(Exception e) {
        return getResponseEntityFromServiceException(
                new ServiceException(e.getMessage(), ErrorCode.UNKNOWN_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(IllegalArgumentException ex) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("message", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.value(), details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(EntityNotFoundException ex) {
        return getResponseEntityFromServiceException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityExistsException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(EntityExistsException ex) {
        return getResponseEntityFromServiceException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidEntityException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(InvalidEntityException ex) {
        return getResponseEntityFromServiceException(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> getResponseEntityFromServiceException(ServiceException ex, HttpStatus httpStatus) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("message", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), ex.getErrorCode().getValue(),
                httpStatus.value(), details);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    private ResponseEntity<ErrorResponse> getResponseEntityFromErrors(List<String> errors) {
        Map<String, Object> details = new LinkedHashMap<>();

        details.put("errors", errors);

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.value(), details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
