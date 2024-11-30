package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.model.dto.ApiErrorDto;
import ru.practicum.ewm.model.exception.BadParametersException;
import ru.practicum.ewm.model.exception.ConflictException;
import ru.practicum.ewm.model.exception.ForbiddenException;
import ru.practicum.ewm.model.exception.NotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDto handleIntegrityViolation(final DataIntegrityViolationException e) {
        log.info("Integrity violation exception - {}", e.getMessage(), e);
        return new ApiErrorDto(HttpStatus.CONFLICT.toString(),
                "Conflict error",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDto handleNotFoundError(final NotFoundException e) {
        log.info("Not found error - {}", e.getMessage(), e);
        return new ApiErrorDto(HttpStatus.NOT_FOUND.toString(),
                "Not Found error",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDto handleConflictError(final ConflictException e) {
        log.info("conflict exception error - {}", e.getMessage(), e);
        return new ApiErrorDto(HttpStatus.CONFLICT.toString(),
                "Not Found error",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorDto handleForbiddenError(final ForbiddenException e) {
        log.info("forbidden exception error - {}", e.getMessage(), e);
        return new ApiErrorDto(HttpStatus.FORBIDDEN.toString(),
                "Not Found error",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleNotValidArgument(final MethodArgumentNotValidException e) {
        log.info("forbidden exception error - {}", e.getMessage(), e);
        return new ApiErrorDto(HttpStatus.BAD_REQUEST.toString(),
                "Not valid argument error",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleMissingParameter(final MissingServletRequestParameterException e) {
        log.info("missing parameter error - {}", e.getMessage(), e);
        return new ApiErrorDto(HttpStatus.BAD_REQUEST.toString(),
                "Bad request error",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleBadParameters(final BadParametersException e) {
        log.info("bad parameters error - {}", e.getMessage(), e);
        return new ApiErrorDto(HttpStatus.BAD_REQUEST.toString(),
                "Bad request error",
                e.getMessage(),
                LocalDateTime.now());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorDto handleInternalError(final Throwable e) {
        log.warn("Unexpected error - {}", e.getMessage(), e);
        return new ApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Unexpected error",
                e.getMessage(),
                LocalDateTime.now());
    }
}
