package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.dto.user.NewUserRequest;
import ru.practicum.ewm.model.dto.user.UserDto;

import java.util.List;

public interface UsersService {
    List<UserDto> getByIds(int[] ids, Pageable pageable);

    UserDto create(NewUserRequest newUser);

    void delete(Long id);
}
