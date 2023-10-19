package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn("Validation exception: ", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionException(final NotFoundException e) {
        log.warn("NotFoundException exception: ", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse notUniqueEmailExceptionException(final NotUniqueEmailException e) {
        log.warn("notUniqueEmail exception: ", e);
        return new ErrorResponse(e.getMessage());
    }
}
