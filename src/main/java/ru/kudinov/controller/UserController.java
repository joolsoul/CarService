package ru.kudinov.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kudinov.model.Car;
import ru.kudinov.model.User;
import ru.kudinov.service.CarService;
import ru.kudinov.service.RequestService;
import ru.kudinov.service.UserService;
import ru.kudinov.util.ImageUtil;

import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {

    private final CarService carService;
    private final UserService userService;
    private final ImageUtil imageUtil;
    private final RequestService requestService;

    @Value("${user.file-directory}")
    private String userFileDirectory;

    public UserController(CarService carService, UserService userService, ImageUtil imageUtil, RequestService requestService) {
        this.carService = carService;
        this.userService = userService;
        this.imageUtil = imageUtil;
        this.requestService = requestService;
    }

    // TODO: сделать сброс пароля для админа
    @GetMapping("{userFromPath}")
    public String getPersonalAccount(Model model, @AuthenticationPrincipal User authUser,
                                     @PathVariable(required = false) User userFromPath) {

        if (authUser == null || userFromPath == null) return "redirect:/";

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:/user/" + authUser.getId();
        }
        if ((!userFromPath.getId().equals(authUser.getId()) && authUser.isAdmin())) {
            model.addAttribute("adminOnUserPage", true);
        }

        Iterable<Car> cars = carService.findByOwner(userFromPath);

        model.addAttribute("cars", cars);
        model.addAttribute("user", userFromPath);
        model.addAttribute("authUser", authUser);
        model.addAttribute("requests", requestService.findAllRequestsWithoutNotConf(userFromPath));

        return "personalAccount";
    }

    @PostMapping("{userFromPath}/addCar")
    public String addCar(@ModelAttribute Car car,
                         @PathVariable(required = false) User userFromPath,
                         @AuthenticationPrincipal User authUser,
                         RedirectAttributes redirectAttributes) {

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:/user/" + authUser.getId();
        }

        car.setOwner(userFromPath);
        car.setRegistrationNumber(car.getRegistrationNumber().toUpperCase());

        if (!carService.isCarDataCorrectly(car)) {
            redirectAttributes.addFlashAttribute("message", "Некорректный регистрационный номер автомобиля");
            redirectAttributes.addFlashAttribute("carToAdd", car);
            return "redirect:/user/" + userFromPath.getId();
        }
        if (!carService.saveCar(car)) {
            redirectAttributes.addFlashAttribute("message", "Автомобиль с данным регистрационым номером уже добавлен");
            redirectAttributes.addFlashAttribute("carToAdd", car);
            return "redirect:/user/" + userFromPath.getId();
        }

        redirectAttributes.addFlashAttribute("message", "Автомобиль успешно добавлен");
        return "redirect:/user/" + userFromPath.getId();
    }

    @GetMapping("{userFromPath}/deleteCar/{car}")
    public String deleteCar(@PathVariable(required = false) Car car,
                            @PathVariable(required = false) User userFromPath,
                            @AuthenticationPrincipal User authUser,
                            RedirectAttributes redirectAttributes) {

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:/user/" + authUser.getId();
        }

        if (!carService.deleteCar(car)) {
            redirectAttributes.addFlashAttribute("message", "Неизвестная ошибка");
        } else redirectAttributes.addFlashAttribute("message", "Машина успешно удалена");

        return "redirect:/user/" + userFromPath.getId();
    }

    @GetMapping("{userFromPath}/editCar/{car}")
    public String getCarEditPage(Model model, @PathVariable(required = false) Car car,
                                 @PathVariable(required = false) User userFromPath,
                                 @AuthenticationPrincipal User authUser) {

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:/user/" + authUser.getId();
        }

        if (car == null || !carService.findByOwner(userFromPath).contains(car)) {
            model.addAttribute("message", "У Вас нет доступа к данному автомобилю");
            model.addAttribute("car", new Car());
            model.addAttribute("user", userFromPath);
            return "editCar";
        }

        model.addAttribute("car", car);
        model.addAttribute("user", userFromPath);

        return "editCar";
    }

    @PostMapping("{userFromPath}/editCar/{car}")
    public String editCar(Model model, @ModelAttribute Car carToChange,
                          @PathVariable(required = false) User userFromPath,
                          @AuthenticationPrincipal User authUser) {

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:/user/" + authUser.getId();
        }

        model.addAttribute("car", carToChange);
        model.addAttribute("user", userFromPath);

        if (!carService.findByOwner(userFromPath).contains(carToChange)) {
            model.addAttribute("message", "У Вас нет прав на редактирование данного автомобиля");
            return "editCar";
        }

        if (!carService.editCar(carToChange)) {
            model.addAttribute("message", "Некорректный регистрационный номер автомобиля");
            return "editCar";
        }

        model.addAttribute("message", "Автомобиль успешно изменён");

        return "editCar";
    }

    @GetMapping("{userFromPath}-changePassword")
    public String getChangePasswordPage(Model model, @AuthenticationPrincipal User authUser,
                                        @PathVariable(required = false) User userFromPath) {

        if (authUser == null || userFromPath == null) return "redirect:/";

        if ((!userFromPath.getId().equals(authUser.getId()))) {
            return "redirect:/user/" + authUser.getId();
        }

        model.addAttribute("user", userFromPath);

        return "changePassword";
    }

    @PostMapping("{userFromPath}-changePassword")
    public String changePassword(Model model, @AuthenticationPrincipal User authUser,
                                 @RequestParam(name = "oldPassword") String oldPassword,
                                 @RequestParam(name = "newPassword") String newPassword,
                                 @RequestParam(name = "passwordConfirm") String passwordConfirm,
                                 @PathVariable(required = false) User userFromPath) {

        model.addAttribute("user", authUser);

        if (userFromPath == null || !userFromPath.getId().equals(authUser.getId())) {
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

    @GetMapping("{userFromPath}-changeLogin")
    public String getChangeLoginPage(Model model, @AuthenticationPrincipal User authUser, @PathVariable(required = false) User userFromPath) {

        if (authUser == null || userFromPath == null) return "redirect:/";

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:" + authUser.getId() + "-changeLogin";
        }

        model.addAttribute("user", userFromPath);

        return "changeLogin";
    }

    @PostMapping("{userFromPath}-changeLogin")
    public String changeLogin(Model model, @PathVariable(required = false) User userFromPath,
                              @AuthenticationPrincipal User authUser,
                              @RequestParam(name = "newLogin") String newLogin) {

        if (authUser == null || userFromPath == null) return "redirect:/";

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:" + authUser.getId() + "-changeLogin";
        }

        model.addAttribute("user", userFromPath);

        if (!userService.changeLogin(userFromPath, newLogin)) {
            model.addAttribute("message", "Логин занят");
            model.addAttribute("newLogin", newLogin);
            return "changeLogin";
        }

        if (userFromPath.getId().equals(authUser.getId())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        model.addAttribute("message", "Логин успешно изменён");

        return "changeLogin";
    }

    @GetMapping("{userFromPath}-edit")
    public String getUserEditPage(Model model, @AuthenticationPrincipal User authUser,
                                  @PathVariable(required = false) User userFromPath) {

        if (userFromPath == null || !userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin()) {
            return "redirect:/user/" + authUser.getId() + "-edit";
        }
        model.addAttribute("user", userFromPath);

        return "editUser";
    }

    //TODO добавление почты
    @PostMapping("{user}-edit")
    public String editUser(Model model, @AuthenticationPrincipal User authUser,
                           @ModelAttribute User changeUser) {

        model.addAttribute("user", changeUser);

        if (!changeUser.getId().equals(authUser.getId()) && !authUser.isAdmin()) {
            model.addAttribute("message", "У Вас нет прав для редактирования данного пользователя");
            return "editUser";
        }

        if (!userService.updateUser(changeUser)) {
            model.addAttribute("message", "Введены некорректные данные");
            model.addAttribute("user", changeUser);
            return "editUser";
        }

        if (changeUser.getId().equals(authUser.getId())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(changeUser, changeUser.getPassword(), changeUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        model.addAttribute("message", "Изменения успешно сохранены");

        return "editUser";
    }

    @PostMapping("{userFromPath}-changeImage")
    public String changeImage(Model model, @AuthenticationPrincipal User authUser,
                              @PathVariable User userFromPath,
                              @RequestParam(value = "uploadImage", required = false) MultipartFile uploadImage) throws IOException {

        if (authUser == null || userFromPath == null) return "redirect:/";

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:/user/" + authUser.getId();
        }

        String fileName = "user_" + userFromPath.getId() + "_profileImage.png";
        userFromPath.setImage(imageUtil.loadImage(uploadImage, userFileDirectory, fileName));
        userService.updateUser(userFromPath);

        if (userFromPath.getId().equals(authUser.getId())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        model.addAttribute("message", "Фото изменено");

        return "redirect:/user/{userFromPath}";
    }

    @GetMapping("{userFromPath}-deleteImage")
    public String deleteImage(@AuthenticationPrincipal User authUser,
                              @PathVariable User userFromPath) throws IOException {

        if (authUser == null || userFromPath == null) return "redirect:/";

        if ((!userFromPath.getId().equals(authUser.getId()) && !authUser.isAdmin())) {
            return "redirect:/user/" + authUser.getId();
        }

        imageUtil.deleteFile(userFromPath.getImage());
        imageUtil.setUserDefaultImage(userFromPath);
        userService.updateUser(userFromPath);

        if (userFromPath.getId().equals(authUser.getId())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        return "redirect:/user/{userFromPath}";
    }
}
