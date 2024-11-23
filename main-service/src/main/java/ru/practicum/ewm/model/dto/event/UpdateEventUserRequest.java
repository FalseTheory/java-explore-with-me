package ru.practicum.ewm.model.dto.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.EventStateUserAction;
import ru.practicum.ewm.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participationLimit;
    private Boolean requestModeration;
    private EventStateUserAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
