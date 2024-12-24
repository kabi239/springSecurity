package com.example.securitydemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class greetingController {
    @GetMapping("/hello/{name}")
    public  String greet(@PathVariable String name) {
        return "Hello "+name+"!";
    }
}
