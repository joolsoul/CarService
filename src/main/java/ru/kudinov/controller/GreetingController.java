package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kudinov.model.Car;
import ru.kudinov.model.User;
import ru.kudinov.model.util.DbFiller;
import ru.kudinov.repository.CarRepository;
import ru.kudinov.service.UserService;

@Controller
public class GreetingController {
    UserService userService;

    public GreetingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String greeting(Model model,@AuthenticationPrincipal User user) {

        DbFiller dbFiller = new DbFiller(userService);
        dbFiller.addUsers();
        model.addAttribute("user", user);

        return "hello";
    }
}