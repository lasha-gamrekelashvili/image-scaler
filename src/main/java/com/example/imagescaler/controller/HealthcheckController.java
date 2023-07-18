package com.example.imagescaler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/healthcheck")
public class HealthcheckController {

    @GetMapping("")
    public ResponseEntity<String> healthcheck(){
        return new ResponseEntity<>("Service up and running.", HttpStatus.OK);
    }
}
