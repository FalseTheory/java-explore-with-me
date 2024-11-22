package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.model.dto.ApiErrorDto;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {


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
