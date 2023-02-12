package ru.kudinov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kudinov.model.User;
import ru.kudinov.service.UserService;
import ru.kudinov.util.ImageUtil;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final ImageUtil imageUtil;

    public RegistrationController(UserService userService, ImageUtil imageUtil) {
        this.userService = userService;
        this.imageUtil = imageUtil;
    }

    @GetMapping("/registration")
    public String getRegistrationPage() {

        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute User user, Model model,
                          @RequestParam(name = "passwordConfirm") String passwordConfirm,
                          RedirectAttributes redirectAttributes) {

        imageUtil.setUserDefaultImage(user);

        model.addAttribute("passwordConfirm", passwordConfirm);

        if (!userService.isUserCorrectly(user)) {
            model.addAttribute("message", "Введены некорректные данные");
            return "registration";
        }

        if (!passwordConfirm.equals(user.getPassword())) {
            model.addAttribute("message", "Пароли не совпадают");
            return "registration";
        }

        if (!userService.saveUser(user)) {
            model.addAttribute("message", "Пользователь с таким именем уже существует");
            return "registration";
        }

        redirectAttributes.addFlashAttribute("username", user.getUsername());
        redirectAttributes.addFlashAttribute("password", user.getUsername());

        return "redirect:login";
    }
}
