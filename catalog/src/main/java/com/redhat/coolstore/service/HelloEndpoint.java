package com.redhat.coolstore.service;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/hello")
public class HelloEndpoint {


    @ResponseBody
    @GetMapping
    public String sayHello() {
        return "Hello, World!";
    }

}
