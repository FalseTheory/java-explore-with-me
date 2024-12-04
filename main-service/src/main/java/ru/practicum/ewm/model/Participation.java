package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.util.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "participations")
@Getter
@Setter
@NoArgsConstructor
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime created;
    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;
    @JoinColumn(name = "requester_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;
    @Enumerated(value = EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

}
