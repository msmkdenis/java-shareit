package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<Response> handleException(ValidateException e) {
        log.error("Ошибка 400: {}", e.getMessage(), e.getCause());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<String> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error("EntityNotFoundException. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        log.error("Throwable. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>("Произошла непредвиденная ошибка.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final BookingStateException e) {
        log.error("BookingStateException. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("Unknown state: UNSUPPORTED_STATUS", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessageFailedException.class)
    public ResponseEntity<Map<String, String>> handleException(MessageFailedException e) {
        log.error("Ошибка 400: {}", e.getMessage(), e.getCause());
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
