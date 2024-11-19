package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"uri", "app", "ip"})
@Table(name = "stats")
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "uri")
    private String uri;
    @Column(nullable = false, name = "ip")
    private String ip;
    @Column(nullable = false, name = "app")
    private String app;
    @Column(nullable = false, name = "timestamp")
    private Timestamp timestamp;

}
