package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kudinov.model.*;
import ru.kudinov.service.*;
import ru.kudinov.util.CartUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartUtil cartUtil;
    private final EmployeeService employeeService;
    private final ServOrganizationService servOrganizationService;
    private final CarService carService;
    private final DetailRequestService detailRequestService;
    private final ServRequestService servRequestService;

    public CartController(CartUtil cartUtil, EmployeeService employeeService, ServOrganizationService
            servOrganizationService, CarService carService, DetailRequestService detailRequestService,
                          ServRequestService servRequestService) {
        this.cartUtil = cartUtil;
        this.employeeService = employeeService;
        this.servOrganizationService = servOrganizationService;
        this.carService = carService;
        this.detailRequestService = detailRequestService;
        this.servRequestService = servRequestService;

    }

    @GetMapping
    public String getCartPage(Model model, @AuthenticationPrincipal User user,
                              HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        cartUtil.createUserCart(model, user, httpRequest, httpResponse);
        model.addAttribute("employees", employeeService.allEmployees());
        if (user != null) model.addAttribute("userCars", carService.findByOwner(user));
        model.addAttribute("services", servOrganizationService.allServiceOrganizations());

        return "cart";
    }

    @GetMapping("/removeDetailRequestFromCart/{detailRequest}")
    public String removeDetailRequestFromCart(@PathVariable DetailRequest detailRequest, @AuthenticationPrincipal User user) {
        if (user != null) cartUtil.removeProducibleWithAuthUser(detailRequest);
        return "redirect:/cart";
    }

    @GetMapping("/removeDetailFromCart/{detail}")
    public String removeDetailFromCart(@PathVariable Detail detail, HttpServletResponse httpResponse) {
        cartUtil.removeProducibleWithNonAuthUser(detail, httpResponse);
        return "redirect:/cart";
    }

    @GetMapping("/removeServiceRequestFromCart/{serviceRequest}")
    public String removeServiceRequestFromCart(@PathVariable ServiceRequest serviceRequest, @AuthenticationPrincipal User user) {
        if (user != null) cartUtil.removeProducibleWithAuthUser(serviceRequest);
        return "redirect:/cart";
    }

    @GetMapping("/removeServiceFromCart/{service}")
    public String removeServiceFromCart(@PathVariable Service service, HttpServletResponse httpResponse) {
        cartUtil.removeProducibleWithNonAuthUser(service, httpResponse);
        return "redirect:/cart";
    }

    @PostMapping("/confirmRequest")
    public String confirmRequest(@AuthenticationPrincipal User user, @RequestParam Map<String, String> form,
                                 @RequestParam Car selectCar, @RequestParam ServiceOrganization selectServiceOrganization) {

        cartUtil.confirmRequest(user, form, selectCar, selectServiceOrganization);
        return "redirect:/user/" + user.getId();
    }
}
