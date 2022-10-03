package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kudinov.model.User;
import ru.kudinov.service.UserService;

@Controller
public class GreetingController {
    UserService userService;

    public GreetingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String greeting(Model model,@AuthenticationPrincipal User user) {

        model.addAttribute("user", user);

        return "hello";
    }
}