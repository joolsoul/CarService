package ru.kudinov.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kudinov.model.Car;
import ru.kudinov.model.User;
import ru.kudinov.repository.CarRepository;
import ru.kudinov.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final CarRepository carRepository;
    private final UserService userService;

    public UserController(CarRepository carRepository, UserService userService) {
        this.carRepository = carRepository;
        this.userService = userService;
    }

    @GetMapping("{user}")
    public String userList(Model model, @AuthenticationPrincipal User user) {

        Iterable<Car> cars = carRepository.findByOwner(user);

        model.addAttribute("cars", cars);
        model.addAttribute("user", user);

        return "personalAccount";
    }

    @PostMapping("{user}")
    public String addCar(@ModelAttribute Car car,
                         @AuthenticationPrincipal User user) {
        car.setOwner(user);

        carRepository.save(car);

        return "redirect:/user/{user}";
    }

    @GetMapping("deleteCar/{car}")
    public String deleteCar(@PathVariable Car car,
                            @AuthenticationPrincipal User user) {


        carRepository.delete(car);

        return "redirect:/user/" + user.getId();
    }

    @GetMapping("editCar/{car}")
    public String editCar(Model model, @PathVariable Car car,
                            @AuthenticationPrincipal User user) {

        model.addAttribute( "car", car);
        model.addAttribute( "user", user);

        return "carEdit";
    }

    @PostMapping("editCar/{car}")
    public String saveCar(@ModelAttribute Car car,
                          @AuthenticationPrincipal User user) {

        carRepository.save(car);

        return "redirect:/user/" + user.getId();
    }

    @GetMapping("{user}-changePassword")
    public String changePassword(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute( "user", user);

        return "changePassword";
    }

    @PostMapping("{user}-changePassword")
    public String savePassword(Model model, @AuthenticationPrincipal User user,
                               @RequestParam(name = "oldPassword") String oldPassword,
                               @RequestParam(name = "newPassword") String newPassword,
                               @RequestParam(name = "passwordConfirm") String passwordConfirm) {

        model.addAttribute( "user", user);

        if (!newPassword.equals(passwordConfirm)) {
            model.addAttribute("message", "Новые пароли не совпадают");
            return "changePassword";
        }

        if (oldPassword.equals(newPassword)) {
            model.addAttribute("message", "Введенные пароли совпадают");
            return "changePassword";
        }

        if (!userService.changePassword(oldPassword, newPassword, user)) {
            model.addAttribute("message", "Текущий пароль не верный");
            return "changePassword";
        }

        model.addAttribute( "message", "Пароль успешно изменён");

        return "changePassword";
    }

    @GetMapping("{user}-changeLogin")
    public String changeLogin(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute( "user", user);

        return "changeLogin";
    }

    @PostMapping("{user}-changeLogin")
    public String saveLogin(Model model, @AuthenticationPrincipal User user,
                               @RequestParam(name = "newLogin") String newLogin) {

        model.addAttribute("user", user);

        if (!userService.changeLogin(user, newLogin)) {
            model.addAttribute("message", "Логин занят");
            return "changeLogin";
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute( "message", "Логин успешно изменён");

        return "changeLogin";
    }

    @GetMapping("{user}-edit")
    public String editUser(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute( "user", user);

        return "userEdit";
    }

    @PostMapping("{user}-edit")
    public String saveUser(Model model, @AuthenticationPrincipal User user,
                           @ModelAttribute User changeUser) {

        model.addAttribute("user", user);

        if (!userService.updateUser(user, changeUser)) {
            model.addAttribute("message", "Введены некорректные данные");
            return "userEdit";
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        model.addAttribute("message", "Изменения успешно сохранены");

        return "userEdit";
    }
}
