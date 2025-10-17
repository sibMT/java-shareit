package ru.practicum.shareit.user;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toNewEntity(UserDto userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(UserDto dto, @MappingTarget User target);
}

