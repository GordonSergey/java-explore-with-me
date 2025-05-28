package ru.practicum.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.rating.model.RatingType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewRatingDto {

    @jakarta.validation.constraints.NotNull(message = "Поле rating не может быть пустым")
    private RatingType rating;
}