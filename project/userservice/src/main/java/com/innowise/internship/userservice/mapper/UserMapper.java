package com.innowise.internship.userservice.mapper;

import com.innowise.internship.userservice.dto.CreateUserRequestDto;
import com.innowise.internship.userservice.dto.UserResponseDto;
import com.innowise.internship.userservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto (User user);

    User toEntity (CreateUserRequestDto createUserRequestDto);
}
