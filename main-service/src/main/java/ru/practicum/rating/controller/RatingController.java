package ru.practicum.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.model.RatingSortType;
import ru.practicum.rating.service.RatingService;
import ru.practicum.user.dto.UserDto;
import ru.practicum.util.Constants;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/users/{userId}/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addRating(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @RequestBody @Valid NewRatingDto ratingDto) {
        return ratingService.addRating(userId, eventId, ratingDto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/rating/{ratingId}")
    public EventFullDto updateRating(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @PathVariable Long ratingId,
                                     @RequestBody @Valid RatingDto ratingDto) {
        return ratingService.updateRating(userId, eventId, ratingId, ratingDto);
    }

    @DeleteMapping("/admin/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto deleteRating(@PathVariable Long eventId) {
        return ratingService.deleteRating(eventId);
    }

    @GetMapping("/events/rating")
    public List<EventShortDto> getEventsByRating(@RequestParam(required = false) String sort,
                                                 @RequestParam(defaultValue = Constants.DEFAULT_FROM_VALUE) int from,
                                                 @RequestParam(defaultValue = Constants.DEFAULT_SIZE_VALUE) int size) {
        RatingSortType sortType = RatingSortType.from(sort);
        Sort.Direction direction = sortType == RatingSortType.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        log.debug("Сортировка событий по рейтингу: {}", direction);
        return ratingService.getEventsByRating(Sort.by(direction, "rating"), from, size);
    }

    @GetMapping("/users/rating")
    public List<UserDto> getUsersByRating(@RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = Constants.DEFAULT_FROM_VALUE) int from,
                                          @RequestParam(defaultValue = Constants.DEFAULT_SIZE_VALUE) int size) {
        RatingSortType sortType = RatingSortType.from(sort);
        Sort.Direction direction = sortType == RatingSortType.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        log.debug("Сортировка пользователей по рейтингу: {}", direction);
        return ratingService.getUsersByRating(Sort.by(direction, "rating"), from, size);
    }
}