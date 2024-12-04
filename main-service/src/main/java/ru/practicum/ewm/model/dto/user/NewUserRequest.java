package ru.practicum.ewm.model.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @Email
    @Size(min = 6, max = 254)
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
