package com.cartoonkitchen.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAny(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", ex.getClass().getSimpleName(),
                "message", Optional.ofNullable(ex.getMessage()).orElse("Unexpected error")
        ));
    }
}
