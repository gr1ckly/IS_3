package org.example.lab1.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorHandlerController {

    @RequestMapping("/err")
    public ResponseEntity<Void> handleError(HttpServletRequest request) {
        Object statusObj = request.getAttribute("jakarta.servlet.error.status_code");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (statusObj instanceof Integer code) {
            status = HttpStatus.resolve(code);
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return ResponseEntity.status(status).build();
    }
}
