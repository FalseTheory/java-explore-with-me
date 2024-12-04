package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.Participation;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long>,
        QuerydslPredicateExecutor<Participation> {

    @Override
    @EntityGraph(attributePaths = {"requester", "event"})
    Optional<Participation> findById(Long aLong);
}
