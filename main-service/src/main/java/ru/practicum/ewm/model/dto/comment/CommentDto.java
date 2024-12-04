package ru.practicum.ewm.model.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.dto.event.EventShortDto;
import ru.practicum.ewm.model.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private UserShortDto author;
    private EventShortDto event;
    private LocalDateTime publishedAt;
    private String text;
    private Boolean edited;
}
