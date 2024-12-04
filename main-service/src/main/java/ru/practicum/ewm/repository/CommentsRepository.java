package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.Comment;

public interface CommentsRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
}
