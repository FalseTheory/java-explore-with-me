package ru.practicum.ewm.model.dto.event;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.util.EventStateAdminAction;
import ru.practicum.ewm.model.util.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateAdminAction stateAction;
    @Size(min = 3, max = 120)
    private String title;

    @AssertTrue
    public boolean isValidDate() {
        if (eventDate == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(eventDate, datePattern).isAfter(now.plusHours(2));
    }
}
