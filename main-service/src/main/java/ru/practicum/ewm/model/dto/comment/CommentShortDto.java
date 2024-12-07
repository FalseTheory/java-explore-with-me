package ru.practicum.ewm.model.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {

    private UserShortDto author;

    private Long eventId;

    private String text;

    private LocalDateTime publishedAt;
    private Boolean edited;
}
