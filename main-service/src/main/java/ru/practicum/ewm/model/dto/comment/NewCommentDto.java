package ru.practicum.ewm.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    private Long eventId;
    private Long authorId;
    @NotBlank
    @Size(min = 10, max = 7000)
    private String text;
}
