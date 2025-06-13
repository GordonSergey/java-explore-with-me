package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.util.List;

import static ru.practicum.util.Constants.DEFAULT_FROM_VALUE;
import static ru.practicum.util.Constants.DEFAULT_SIZE_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = DEFAULT_FROM_VALUE) int from,
                                  @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) int size) {
        if (ids == null) {
            return userService.getUsers(from, size);
        }
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Validated NewUserRequestDto newUserRequestDto) {
        return userService.createUser(newUserRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/rating")
    public List<UserDto> getUsersByRating(@RequestParam(defaultValue = "DESC") String sort,
                                          @RequestParam(defaultValue = DEFAULT_FROM_VALUE) int from,
                                          @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) int size) {
        return userService.getUsersByRating(sort, from, size);
    }
}