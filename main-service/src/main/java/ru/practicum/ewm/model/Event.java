package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.util.EventState;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;
    private Float lat;
    private Float lon;
    private Boolean paid;
    @Column(name = "participation_limit")
    private Integer participationLimit;
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EventState state = EventState.PENDING;
    private String title;
    private Long views = 0L;

}
