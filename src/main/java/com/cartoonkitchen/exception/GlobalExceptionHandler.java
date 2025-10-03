package com.cartoonkitchen.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/")) {
            LoggerFactory.getLogger("api-logger").error("Ошибка в API-запросе: " + uri, ex);
            return "forward:/error/json";
        } else {
            log.error("Ошибка в обычном запросе: " + uri, ex);
            model.addAttribute("message", ex.getMessage());
            return "error";
        }
    }

}
