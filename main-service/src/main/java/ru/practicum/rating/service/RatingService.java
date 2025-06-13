package ru.practicum.rating.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.user.dto.UserDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface RatingService {

    EventFullDto addRating(Long userId, Long eventId, NewRatingDto ratingDto);

    EventFullDto updateRating(Long userId, Long eventId, Long ratingId, RatingDto ratingDto);

    EventFullDto deleteRating(Long eventId);

    List<EventShortDto> getEventsByRating(Sort sort, int from, int size);

    List<UserDto> getUsersByRating(Sort sort, int from, int size);
}