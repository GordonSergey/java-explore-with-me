package ru.practicum.rating.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RatingSortType {
    ASC,
    DESC;

    @JsonCreator
    public static RatingSortType from(String value) {
        if (value == null) {
            return DESC;
        }
        try {
            return RatingSortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимое значение sort: " + value +
                                                       ". Разрешены только ASC или DESC.");
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}