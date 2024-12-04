package ru.practicum.ewm.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDto {
    private String status;
    private String reason;
    private String messageError;
    private LocalDateTime timestamp;
}
