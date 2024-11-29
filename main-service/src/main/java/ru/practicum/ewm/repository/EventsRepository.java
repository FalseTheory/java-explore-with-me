package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.Event;

public interface EventsRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
}
