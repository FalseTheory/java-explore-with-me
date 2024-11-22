package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.service.StatService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
class StatsControllerTest {

    @MockBean
    private StatService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private final ParamHitDto paramHitDto = new ParamHitDto(
            "ewm-main-service",
            "/events/1",
            "192.163.0.1",
            null
    );

    @Test
    @DisplayName("Эндпоинт POST /hit должен корректно работать")
    void hitTest() throws Exception {
        when(service.hit(any()))
                .thenReturn(paramHitDto);

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(paramHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("app").value(paramHitDto.getApp()))
                .andExpect(jsonPath("uri").value(paramHitDto.getUri()))
                .andExpect(jsonPath("ip").value(paramHitDto.getIp()));

        verify(service, times(1)).hit(any());
    }

    @Test
    @DisplayName("Эндпоинт GET /stats должен корректно работать")
    void getAllTest() throws Exception {
        when(service.getAll(anyString(), anyString(), any(), anyBoolean()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/stats")
                        .param("start", "")
                        .param("end", "")
                        .param("uris", "test")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}