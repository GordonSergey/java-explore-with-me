package ru.practicum.rating.mapper;

import ru.practicum.exceptions.ValidationRequestException;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.model.Rating;
import ru.practicum.rating.model.RatingType;

public class RatingMapper {

    public static Rating toRating(RatingDto dto) {
        if (dto == null) return null;

        RatingType type;
        try {
            type = RatingType.valueOf(dto.getRating());
        } catch (IllegalArgumentException e) {
            throw new ValidationRequestException("Недопустимое значение рейтинга: " + dto.getRating());
        }

        return Rating.builder()
                     .rating(type)
                     .build();
    }

    public static RatingDto toRatingDto(Rating rating) {
        if (rating == null) return null;
        return RatingDto.builder()
                        .id(rating.getId())
                        .rating(rating.getRating().name())
                        .build();
    }
}