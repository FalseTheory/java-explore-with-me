package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

import ru.practicum.ewm.dto.ApiErrorDto;

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
        return new ApiErrorDto("Unexpected error", stackTrace);
    }
}