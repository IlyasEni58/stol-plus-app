package ru.enikeev.stolplusapp.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка кастомных NotFound исключений
     * Возвращает 404 NOT_FOUND с понятным сообщением
     */
    @ExceptionHandler({
            UserNotFoundException.class,
            OrderNotFoundException.class,
            FurnitureNotFoundException.class,
            CategoryNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(RuntimeException ex) {
        log.warn("Ресурс не найден: {}", ex.getMessage());

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage()
        );
    }

    /**
     * Обработка ошибок валидации @Valid
     * Возвращает 400 BAD_REQUEST с деталями по каждому полю
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Ошибка валидации данных",
                errors
        );
    }

    /**
     * Обработка ConstraintViolationException (валидация параметров @RequestParam, @PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Ошибка валидации параметров: {}", ex.getMessage());

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PARAMETER",
                ex.getMessage()
        );
    }

    /**
     * Обработка бизнес-логики исключений (IllegalArgumentException, IllegalStateException)
     * Возвращает 400 BAD_REQUEST - клиент отправил некорректные данные
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusinessLogicException(RuntimeException ex) {
        log.warn("Ошибка бизнес-логики: {}", ex.getMessage());

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "BUSINESS_ERROR",
                ex.getMessage()
        );
    }

    /**
     * Обработка всех непредвиденных исключений
     * Возвращает 500 INTERNAL_SERVER_ERROR - скрываем детали от клиента
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception ex) {
        log.error("Непредвиденная ошибка: ", ex); // Логируем полный stacktrace

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "Внутренняя ошибка сервера. Пожалуйста, обратитесь к администратору."
        );
    }

    /**
     * Обработка NullPointerException отдельно (частая ошибка)
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNullPointerException(NullPointerException ex) {
        log.error("NullPointerException: ", ex);

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "NULL_POINTER_ERROR",
                "Внутренняя ошибка сервера. Пожалуйста, обратитесь к администратору."
        );
    }

}
