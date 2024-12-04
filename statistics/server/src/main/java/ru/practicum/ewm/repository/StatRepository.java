package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("select s from Stat s where (s.timestamp >= ?1) and (s.timestamp < ?2)")
    List<Stat> findByDate(LocalDateTime start, LocalDateTime end);

    @Query("select s from Stat s where (s.timestamp >= ?1) and (s.timestamp < ?2) " +
            "and s.uri in (?3)")
    List<Stat> findByDateAndUri(LocalDateTime start, LocalDateTime end, String[] uris);

}
