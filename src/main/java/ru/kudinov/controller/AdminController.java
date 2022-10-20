package ru.kudinov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kudinov.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserListPage(Model model) {

        model.addAttribute("users", userService.allUsers());

        return "userList";
    }


    //TODO: Сделать отображения информации о конкретном пользователе, основываясь на pathVariable
    //TODO: редактирование машин, информации о пользователе и его прав
    @GetMapping("/user/{user}")
    public String getUserPage(Model model, @PathVariable String user) {
        return "index";
    }

}
