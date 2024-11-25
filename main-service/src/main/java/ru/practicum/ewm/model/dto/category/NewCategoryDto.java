package ru.practicum.ewm.model.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @NotNull
    @Size(min = 1, max = 50)
    private String name;
}
