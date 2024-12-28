package com.example.securitydemo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class greetingController {
    @GetMapping("/hello/{name}")
    public  String greet(@PathVariable String name) {
        return "Hello "+name+"!";
    }

    @PreAuthorize("hasRole('USER')")
    //Ensures that only authorized users can invoke the annotated method
    @GetMapping("/user/{name}")
    public  String userEndpoint(@PathVariable String name) {

        return "Hello User "+name+"!";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{name}")
    public  String adminEndpoint(@PathVariable String name) {

        return "Hello Admin "+name+"!";
    }


}
