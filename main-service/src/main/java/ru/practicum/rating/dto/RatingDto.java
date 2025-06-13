package ru.practicum.rating.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingDto {
    private Long id;

    @NotBlank(message = "Поле 'rating' не может быть пустым")
    private String rating;
}