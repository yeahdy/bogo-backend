package com.boardgo.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping(value = "/health")
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok("HEALTH CHECK OK");
    }
}
