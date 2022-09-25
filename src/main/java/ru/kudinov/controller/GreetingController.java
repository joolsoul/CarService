package ru.kudinov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kudinov.model.User;
import ru.kudinov.service.UserService;

import java.util.Map;

@Controller
public class GreetingController {

    private final UserService userService;

    public GreetingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String greeting(Map<String, Object> model) {

        Iterable<User> users = userService.allUsers();

        model.put("users", users);

        return "inner";
    }

}