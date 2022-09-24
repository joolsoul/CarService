package ru.kudinov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kudinov.model.User;
import ru.kudinov.repository.UserRepository;

import java.util.Map;

@Controller
public class GreetingController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public String greeting(Map<String, Object> model) {

        Iterable<User> users = userRepository.findAll();

        model.put("users", users);
        return "hello";
    }

    @PostMapping("/hello")
    public String addUser(@RequestParam String name,  @RequestParam String surname, Map<String, Object> model) {

        User n = new User();
        n.setName(name);
        n.setSurname(surname);
        userRepository.save(n);

        Iterable<User> users = userRepository.findAll();

        model.put("users", users);

        return "hello";
    }
}