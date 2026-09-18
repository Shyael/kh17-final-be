package com.kh.khedu.error;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PayrollCalculationExceptionHandler {

    @ExceptionHandler(PayrollCalculationException.class)
    public ResponseEntity<Map<String, String>> handlePayrollCalculationException(
            PayrollCalculationException exception) {

        Map<String, String> body = new LinkedHashMap<>();

        body.put("code", exception.getCode());
        body.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(body);
    }
}