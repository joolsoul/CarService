package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kudinov.model.Car;
import ru.kudinov.model.Request;
import ru.kudinov.model.User;
import ru.kudinov.service.CarService;
import ru.kudinov.service.DetailRequestService;
import ru.kudinov.service.RequestService;
import ru.kudinov.service.ServRequestService;

import java.util.List;

@Controller
public class RequestController {

    private final CarService carService;
    private final RequestService requestService;
    private final ServRequestService servRequestService;
    private final DetailRequestService detailRequestService;

    public RequestController(CarService carService, RequestService requestService,
                             ServRequestService servRequestService, DetailRequestService detailRequestService) {
        this.carService = carService;
        this.requestService = requestService;
        this.servRequestService = servRequestService;
        this.detailRequestService = detailRequestService;
    }

    @GetMapping("/getRequestsByCar/{car}")
    public String getMyRequestsPage(@AuthenticationPrincipal User user, @PathVariable(required = false) Car car, Model model) {

        model.addAttribute("user", user);

        if (car == null || !carService.findByOwner(user).contains(car)) {
            model.addAttribute("message", "У Вас нет доступа к данному автомобилю");
            model.addAttribute("car", new Car());
            return "myRequests";
        }

        List<Request> requestsByCar = requestService.findAllRequestsByCar(car);
        model.addAttribute("requests", requestsByCar);
        model.addAttribute("car", car);
        return "myRequests";
    }

    //TODO: добавить ссылку на деталь из заказа в магазине
    @GetMapping("/getRequest/{request}")
    public String getRequestPage(@AuthenticationPrincipal User user, @PathVariable(required = false) Request request, Model model) {

        model.addAttribute("user", user);

        if (request == null || !carService.findByOwner(user).contains(request.getCar())) {
            model.addAttribute("message", "У Вас нет доступа к данному заказу");
            return "carRequest";
        }

        model.addAttribute("serviceRequests", servRequestService.getServiceRequestsByRequest(request));
        model.addAttribute("detailRequests", detailRequestService.getDetailRequestsByRequest(request));
        model.addAttribute("request", request);


        return "carRequest";
    }
}
