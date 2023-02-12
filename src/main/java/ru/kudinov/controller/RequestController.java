package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kudinov.model.Car;
import ru.kudinov.model.Request;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.entityEnums.RequestStatus;
import ru.kudinov.service.CarService;
import ru.kudinov.service.DetailRequestService;
import ru.kudinov.service.RequestService;
import ru.kudinov.service.ServRequestService;
import ru.kudinov.util.CartUtil;

@Controller
public class RequestController {

    private final CarService carService;
    private final RequestService requestService;
    private final ServRequestService servRequestService;
    private final DetailRequestService detailRequestService;
    private final CartUtil cartUtil;

    public RequestController(CarService carService, RequestService requestService, ServRequestService servRequestService, DetailRequestService detailRequestService, CartUtil cartUtil) {
        this.carService = carService;
        this.requestService = requestService;
        this.servRequestService = servRequestService;
        this.detailRequestService = detailRequestService;
        this.cartUtil = cartUtil;
    }

    @GetMapping("/getRequestsByCar/{car}")
    public String getMyRequestsPage(@AuthenticationPrincipal User user, @PathVariable(required = false) Car car, Model model) {


        if (car == null || (!carService.findByOwner(user).contains(car) && !user.isAdmin())) {
            model.addAttribute("message", "У Вас нет доступа к данному автомобилю");
            model.addAttribute("car", new Car());
            return "myRequests";
        }

        model.addAttribute("user", car.getOwner());
        model.addAttribute("authUser", user);
        model.addAttribute("requests", requestService.findAllRequestsByCar(car));
        model.addAttribute("car", car);
        return "myRequests";
    }

    @GetMapping("/getRequest/{request}")
    public String getRequestPage(@AuthenticationPrincipal User user, @PathVariable(required = false) Request request, Model model) {

        if (request == null || (!carService.findByOwner(user).contains(request.getCar()) && !user.isAdmin())) {
            model.addAttribute("message", "У Вас нет доступа к данному заказу");
            return "request";
        }

        model.addAttribute("user", request.getUser());
        model.addAttribute("authUser", user);

        model.addAttribute("serviceRequests", servRequestService.getServiceRequestsByRequest(request));
        model.addAttribute("detailRequests", detailRequestService.getDetailRequestsByRequest(request));
        model.addAttribute("request", request);


        return "request";
    }

    @GetMapping("/admin/applyRequest/{request}")
    public String applyRequest(@PathVariable Request request) {

        request.setRequestStatus(RequestStatus.IN_PROGRESS);
        requestService.updateRequest(request);
        return "redirect:/admin/getRequests";
    }


    @GetMapping("cancelRequest/{request}")
    public String cancelRequest(@PathVariable Request request, @AuthenticationPrincipal User user) {

        request.setRequestStatus(RequestStatus.CANCELLED);
        cartUtil.returnDetails(request);
        requestService.updateRequest(request);

        if (user.isAdmin()) return "redirect:/admin/getRequests";
        return "redirect:/user/" + user.getId();
    }

    @GetMapping("admin/deleteRequest/{request}")
    public String deleteRequest(@PathVariable Request request) {

        if (!request.isRequestCancelled()) cartUtil.returnDetails(request);
        requestService.deleteRequest(request);

        return "redirect:/user/" + request.getUser().getId();
    }
}
