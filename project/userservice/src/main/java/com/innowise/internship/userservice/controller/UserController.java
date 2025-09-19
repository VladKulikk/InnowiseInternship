package com.innowise.internship.userservice.controller;

import com.innowise.internship.userservice.dto.CreateUserRequestDto;
import com.innowise.internship.userservice.dto.UserResponseDto;
import com.innowise.internship.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users") // Sets the base URL for all endpoints in this class
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody CreateUserRequestDto requestDto){
        return userService.createUser(requestDto);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @GetMapping("/by-email")
    public UserResponseDto getUserByEmail(@RequestParam String email){
        return userService.getUserByEmail(email);
    }

    @GetMapping
    List<UserResponseDto> getUsersByIds(@RequestParam List<Long> ids){
        return userService.getUsersByIds(ids);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequestDto requestDto){
        return userService.updateUser(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }

}
