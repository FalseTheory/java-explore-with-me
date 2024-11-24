package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.User;

public interface UsersRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User>,
        JpaSpecificationExecutor<User> {


}
