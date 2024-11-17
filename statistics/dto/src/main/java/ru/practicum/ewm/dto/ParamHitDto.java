package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamHitDto {

    @NotNull
    String app;
    @NotNull
    String uri;
    @NotNull
    String ip;
    String timestamp;
}
