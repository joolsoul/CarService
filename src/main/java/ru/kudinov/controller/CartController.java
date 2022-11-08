package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kudinov.model.User;
import ru.kudinov.service.CarService;
import ru.kudinov.service.EmployeeService;
import ru.kudinov.service.ServOrganizationService;
import ru.kudinov.util.CartUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class CartController {

    private final CartUtil cartUtil;
    private final EmployeeService employeeService;
    private final ServOrganizationService servOrganizationService;
    private final CarService carService;

    public CartController(CartUtil cartUtil, EmployeeService employeeService, ServOrganizationService servOrganizationService, CarService carService) {
        this.cartUtil = cartUtil;
        this.employeeService = employeeService;
        this.servOrganizationService = servOrganizationService;
        this.carService = carService;
    }

    @GetMapping("/cart")
    public String getCartPage(Model model, @AuthenticationPrincipal User user,
                              HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        cartUtil.createUserCart(model, user, httpRequest, httpResponse);
        model.addAttribute("employees", employeeService.getEmployees());
        if (user != null) model.addAttribute("userCars", carService.findByOwner(user));
        model.addAttribute("services", servOrganizationService.getServiceOrganizations());

        return "cart";
    }
}
