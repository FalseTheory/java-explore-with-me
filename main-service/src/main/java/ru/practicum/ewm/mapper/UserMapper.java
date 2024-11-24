package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.dto.user.NewUserRequest;
import ru.practicum.ewm.model.dto.user.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto mapToUserDto(User user);

    User mapNewUserToUser(NewUserRequest newUserRequest);
}
