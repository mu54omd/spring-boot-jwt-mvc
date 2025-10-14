package com.musashi.spring_boot_jwt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalValidationHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException exception){

        List<String> errors = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(objectError -> {
                    if(objectError.getDefaultMessage() == null)
                        return "Invalid value";
                    else
                        return objectError.getDefaultMessage();
                    }
                )
                .toList();
        HashMap<String, Object> responseBody = new HashMap<>();
        responseBody.put("errors", errors);
        return ResponseEntity
                .status(400)
                .body(responseBody);

    }
}
