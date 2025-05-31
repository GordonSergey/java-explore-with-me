package ru.practicum.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.category.controller.CategoryController;
import ru.practicum.compilation.controller.CompilationController;
import ru.practicum.event.controller.EventController;
import ru.practicum.rating.controller.RatingController;
import ru.practicum.request.controller.RequestController;
import ru.practicum.user.controller.UserController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice(assignableTypes = {
        UserController.class,
        CategoryController.class,
        EventController.class,
        RequestController.class,
        CompilationController.class,
        RatingController.class
})
public class ErrorHandler {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return buildApiError("BAD_REQUEST", "Неверно составлен запрос.", e.getMessage());
    }

    @ExceptionHandler(ValidationRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationRequestException(ValidationRequestException e) {
        return buildApiError("BAD_REQUEST", "Неверно составлен запрос.", e.getMessage());
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            CategoryNotFoundException.class,
            EventNotFoundException.class,
            RequestNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(EntityNotFoundException e) {
        return buildApiError("NOT_FOUND", "Не найден запрашиваемый объект.", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        return buildApiError("CONFLICT", "Нарушено ограничение целостности.", e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalOrUnreadable(Exception e) {
        return buildApiError("BAD_REQUEST", "Неверный ввод или формат.", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenException(ForbiddenException e) {
        return buildApiError("FORBIDDEN", "Условия для выполнения операции не соблюдены.", e.getMessage());
    }

    private ApiError buildApiError(String status, String reason, String message) {
        return ApiError.builder()
                       .status(status)
                       .reason(reason)
                       .message(message)
                       .timestamp(LocalDateTime.now().format(FORMATTER))
                       .build();
    }
}