package ru.kudinov.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kudinov.model.Car;
import ru.kudinov.model.User;
import ru.kudinov.service.CarService;
import ru.kudinov.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final CarService carService;
    private final UserService userService;

    public UserController(CarService carService, UserService userService) {
        this.carService = carService;
        this.userService = userService;
    }

    @GetMapping("{user}")
    public String userList(Model model, @AuthenticationPrincipal User authUser, @PathVariable(required = false) User user) {

        if (user == null || !user.getId().equals(authUser.getId())) {
            return "redirect:" + authUser.getId();
        }

        Iterable<Car> cars = carService.findByOwner(authUser);

        model.addAttribute("cars", cars);
        model.addAttribute("user", authUser);

        return "personalAccount";
    }

    @PostMapping("{user}")
    public String addCar(@ModelAttribute Car car,
                         @AuthenticationPrincipal User authUser,
                         RedirectAttributes redirectAttributes, @PathVariable(required = false) User user) {

        if (user == null || !user.getId().equals(authUser.getId())) {
            return "redirect:" + authUser.getId();
        }

        car.setOwner(authUser);

        if (!carService.isCarDataCorrectly(car)) {
            redirectAttributes.addFlashAttribute("message", "Некорректный регистрационный номер автомобиля");
            redirectAttributes.addFlashAttribute("carToAdd", car);
            return "redirect:{user}";
        }
        if (!carService.saveCar(car)) {
            redirectAttributes.addFlashAttribute("message", "Автомобиль с данным регистрационым номером уже добавлен");
            redirectAttributes.addFlashAttribute("carToAdd", car);
            return "redirect:{user}";
        }

        redirectAttributes.addFlashAttribute("message", "Автомобиль успешно добавлен");
        return "redirect:{user}";
    }

    @GetMapping("deleteCar/{car}")
    public String deleteCar(@PathVariable Car car,
                            @AuthenticationPrincipal User user,
                            RedirectAttributes redirectAttributes) {

        if (!carService.deleteCar(car)) {
            redirectAttributes.addFlashAttribute("message", "Неизвестная ошибка");
        }

        return "redirect:/user/" + user.getId();
    }

    @GetMapping("editCar/{car}")
    public String editCar(Model model, @PathVariable(required = false) Car car,
                          @AuthenticationPrincipal User user) {

        if (car == null || !carService.findByOwner(user).contains(car)) {
            model.addAttribute("message", "У Вас нет доступа к данному автомобилю");
            model.addAttribute("car", new Car());
            model.addAttribute("user", user);
            return "carEdit";
        }

        model.addAttribute("car", car);
        model.addAttribute("user", user);

        return "carEdit";
    }

    @PostMapping("editCar/{car}")
    public String changeCar(Model model, @ModelAttribute Car carToChange,
                            @AuthenticationPrincipal User user) {

        model.addAttribute("car", carToChange);
        model.addAttribute("user", user);

        if (!carService.findByOwner(user).contains(carToChange)) {
            model.addAttribute("message", "У Вас нет прав на редактирование данного автомобиля");
            return "carEdit";
        }

        if (!carService.editCar(carToChange)) {
            model.addAttribute("message", "Некорректный регистрационный номер автомобиля");
            return "carEdit";
        }

        model.addAttribute("message", "Автомобиль успешно изменён");

        return "carEdit";
    }

    @GetMapping("{user}-changePassword")
    public String changePassword(Model model, @AuthenticationPrincipal User authUser, @PathVariable(required = false) User user) {

        if (user == null || !user.getId().equals(authUser.getId())) {
            return "redirect:" + authUser.getId() + "-changePassword";
        }

        model.addAttribute("user", authUser);

        return "changePassword";
    }

    @PostMapping("{user}-changePassword")
    public String savePassword(Model model, @AuthenticationPrincipal User authUser,
                               @RequestParam(name = "oldPassword") String oldPassword,
                               @RequestParam(name = "newPassword") String newPassword,
                               @RequestParam(name = "passwordConfirm") String passwordConfirm,
                               @PathVariable(required = false) User user) {

        model.addAttribute("user", authUser);

        if (user == null || !user.getId().equals(authUser.getId())) {
            return "redirect:" + authUser.getId() + "-changePassword";
        }

        if (!newPassword.equals(passwordConfirm)) {
            model.addAttribute("message", "Новые пароли не совпадают");
            return "changePassword";
        }

        if (oldPassword.equals(newPassword)) {
            model.addAttribute("message", "Введенные пароли совпадают");
            return "changePassword";
        }

        if (!userService.changePassword(oldPassword, newPassword, authUser)) {
            model.addAttribute("message", "Текущий пароль не верный");
            return "changePassword";
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("message", "Пароль успешно изменён");

        return "changePassword";
    }

    @GetMapping("{user}-changeLogin")
    public String changeLogin(Model model, @AuthenticationPrincipal User authUser, @PathVariable(required = false) User user) {

        if (user == null || !user.getId().equals(authUser.getId())) {
            return "redirect:" + authUser.getId() + "-changeLogin";
        }

        model.addAttribute("user", authUser);

        return "changeLogin";
    }

    @PostMapping("{user}-changeLogin")
    public String saveLogin(Model model, @AuthenticationPrincipal User authUser,
                            @RequestParam(name = "newLogin") String newLogin,
                            @PathVariable(required = false) User user) {

        model.addAttribute("user", authUser);

        if (user == null || !user.getId().equals(authUser.getId())) {
            return "redirect:" + authUser.getId() + "-changeLogin";
        }

        if (!userService.changeLogin(authUser, newLogin)) {
            model.addAttribute("message", "Логин занят");
            model.addAttribute("newLogin", newLogin);
            return "changeLogin";
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("message", "Логин успешно изменён");

        return "changeLogin";
    }

    @GetMapping("{user}-edit")
    public String editUser(Model model, @AuthenticationPrincipal User authUser, @PathVariable(required = false) User user) {

        if (user == null || !user.getId().equals(authUser.getId())) {
            return "redirect:" + authUser.getId() + "-edit";
        }
        model.addAttribute("user", authUser);

        return "userEdit";
    }

    //TODO добавление почты
    @PostMapping("{user}-edit")
    public String saveUser(Model model, @AuthenticationPrincipal User user,
                           @ModelAttribute User changeUser) {

        model.addAttribute("user", changeUser);

        if (!changeUser.getId().equals(user.getId())) {
            model.addAttribute("message", "У Вас нет прав для редактирования данного пользователя");
            return "userEdit";
        }

        if (!userService.updateUser(changeUser)) {
            model.addAttribute("message", "Введены некорректные данные");
            model.addAttribute("user", changeUser);
            return "userEdit";
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(changeUser, changeUser.getPassword(), changeUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        model.addAttribute("message", "Изменения успешно сохранены");

        return "userEdit";
    }
}
