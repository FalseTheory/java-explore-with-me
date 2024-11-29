package ru.practicum.ewm.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.QUser;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.dto.user.NewUserRequest;
import ru.practicum.ewm.model.dto.user.UserDto;
import ru.practicum.ewm.repository.UsersRepository;
import ru.practicum.ewm.service.UsersService;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllFiltered(int[] ids, Pageable pageable) {
        Page<User> users;
        if (ids != null) {
            BooleanExpression byIds = QUser.user.id.in(Arrays.stream(ids).asLongStream().boxed().toList());
            users = repository.findAll(byIds, pageable);
        } else {
            users = repository.findAll(pageable);
        }

        return users.getContent().stream().map(mapper::mapToUserDto).toList();
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUser) {
        return mapper.mapToUserDto(repository.save(mapper.mapNewUserToUser(newUser)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
