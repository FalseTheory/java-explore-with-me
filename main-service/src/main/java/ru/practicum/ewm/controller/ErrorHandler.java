package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.model.dto.ApiErrorDto;
import ru.practicum.ewm.model.exception.NotFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDto handle(final DataIntegrityViolationException e) {
        log.info("Integrity violation exception - {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return new ApiErrorDto(HttpStatus.CONFLICT.toString(),
                "Conflict error",
                sw.toString(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDto handleNotFoundError(final NotFoundException e) {
        log.info("Not found error - {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return new ApiErrorDto(HttpStatus.NOT_FOUND.toString(),
                "Not Found error",
                sw.toString(),
                LocalDateTime.now());
    }


    @ExceptionHandler
    public ApiErrorDto handleInternalError(final Throwable e) {
        log.warn("Unexpected error - {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Unexpected error",
                stackTrace,
                LocalDateTime.now());
    }
}
