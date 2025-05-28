package ru.practicum.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.exceptions.UserNotFoundException;
import ru.practicum.exceptions.ValidationRequestException;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.mapper.RatingMapper;
import ru.practicum.rating.model.Rating;
import ru.practicum.rating.model.RatingType;
import ru.practicum.rating.repository.RatingRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RatingRepository ratingRepository;

    @Override
    @Transactional
    public EventFullDto addRating(Long userId, Long eventId, NewRatingDto ratingDto) {
        log.info("Добавляем рейтинг пользователем {} событию {}: {}", userId, eventId, ratingDto.getRating());

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new UserNotFoundException(userId));
        Event event = eventRepository.findById(eventId)
                                     .orElseThrow(() -> new EventNotFoundException(eventId));

        List<Request> confirmed = requestRepository.findAllByRequesterIdAndEventIdAndStatus(
                userId, eventId, RequestStatus.CONFIRMED);
        if (confirmed.isEmpty()) {
            throw new ValidationRequestException("Пользователь не посещал событие, нельзя поставить рейтинг.");
        }

        if (!ratingRepository.findAllByEventIdAndUserId(eventId, userId).isEmpty()) {
            throw new ValidationRequestException("Рейтинг уже установлен этим пользователем.");
        }

        Rating rating = Rating.builder()
                              .rating(ratingDto.getRating())
                              .event(event)
                              .user(user)
                              .build();
        ratingRepository.save(rating);

        Event updated = updateRatingForEvent(event);
        updateRatingForUser(event);
        return EventMapper.toEventFullDto(updated);
    }

    @Override
    @Transactional
    public EventFullDto updateRating(Long userId, Long eventId, Long ratingId, RatingDto ratingDto) {
        log.info("Обновляем рейтинг id {} пользователем {} событию {} на {}", ratingId, userId, eventId, ratingDto);

        userRepository.findById(userId)
                      .orElseThrow(() -> new UserNotFoundException(userId));
        Event event = eventRepository.findById(eventId)
                                     .orElseThrow(() -> new EventNotFoundException(eventId));
        Rating rating = ratingRepository.findById(ratingId)
                                        .orElseThrow(() -> new ValidationRequestException("Рейтинг с ID " + ratingId + " не найден."));

        if (!rating.getUser().getId().equals(userId) || !rating.getEvent().getId().equals(eventId)) {
            throw new ValidationRequestException("Рейтинг не принадлежит данному пользователю или событию.");
        }

        RatingType newRatingType = RatingMapper.toRating(ratingDto).getRating();

        if (rating.getRating() == newRatingType) {
            throw new ValidationRequestException("Такой рейтинг уже установлен.");
        }

        rating.setRating(newRatingType);
        ratingRepository.save(rating);

        Event updated = updateRatingForEvent(event);
        updateRatingForUser(event);
        return EventMapper.toEventFullDto(updated);
    }

    @Override
    @Transactional
    public EventFullDto deleteRating(Long eventId) {
        log.info("Удаляем все рейтинги события {}", eventId);

        Event event = eventRepository.findById(eventId)
                                     .orElseThrow(() -> new EventNotFoundException(eventId));
        List<Rating> ratings = ratingRepository.findAllByEventId(eventId);

        if (ratings.isEmpty()) {
            log.warn("Попытка удалить рейтинг, но рейтинг отсутствует. Возвращаем актуальные данные.");
            return EventMapper.toEventFullDto(event);
        }

        List<Long> ids = ratings.stream().map(Rating::getId).collect(Collectors.toList());
        ratingRepository.deleteByIdIn(ids);

        Event updated = updateRatingForEvent(event);
        updateRatingForUser(event);
        return EventMapper.toEventFullDto(updated);
    }

    private Event updateRatingForEvent(Event event) {
        long likes = ratingRepository.countRatingByEventIdAndRating(event.getId(), RatingType.LIKE.name());
        long dislikes = ratingRepository.countRatingByEventIdAndRating(event.getId(), RatingType.DISLIKE.name());
        int rating = (int) (likes - dislikes);
        event.setRating(rating);
        return eventRepository.save(event);
    }

    private void updateRatingForUser(Event event) {
        User user = event.getInitiator();
        if (user == null) return;

        long likes = ratingRepository.countRatingByUserIdAndRating(user.getId(), RatingType.LIKE.name());
        long dislikes = ratingRepository.countRatingByUserIdAndRating(user.getId(), RatingType.DISLIKE.name());
        int rating = (int) (likes - dislikes);
        user.setRating(rating);
        userRepository.save(user);
        log.debug("Обновлён рейтинг пользователя (инициатора) id {}: лайки = {}, дизлайки = {}, итог = {}",
                  user.getId(), likes, dislikes, rating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByRating(Sort sort, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;

        if (sort.iterator().hasNext() && sort.iterator().next().isAscending()) {
            events = eventRepository.findAllPublishedEventsOrderByRatingAsc(pageable);
        } else {
            events = eventRepository.findAllPublishedEventsOrderByRatingDesc(pageable);
        }

        return events.stream()
                     .map(EventMapper::toEventShortDto)
                     .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRating(Sort sort, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users;

        if (sort.iterator().hasNext() && sort.iterator().next().isAscending()) {
            users = userRepository.findAllByOrderByRatingAsc(pageable);
        } else {
            users = userRepository.findAllByOrderByRatingDesc(pageable);
        }

        return users.stream()
                    .map(user -> UserDto.builder()
                                        .id(user.getId())
                                        .name(user.getName())
                                        .email(user.getEmail())
                                        .rating(Optional.ofNullable(user.getRating()).orElse(0))
                                        .build())
                    .collect(Collectors.toList());
    }
}