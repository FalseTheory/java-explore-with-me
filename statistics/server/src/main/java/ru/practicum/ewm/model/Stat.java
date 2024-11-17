package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "stats")
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, name = "uri")
    String uri;
    @Column(nullable = false, name = "ip")
    String ip;
    @Column(nullable = false, name = "app")
    String app;
    @Column(nullable = false, name = "timestamp")
    Timestamp timestamp;

}
