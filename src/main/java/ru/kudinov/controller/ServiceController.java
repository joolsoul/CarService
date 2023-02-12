package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kudinov.model.Request;
import ru.kudinov.model.Service;
import ru.kudinov.model.ServiceRequest;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.entityEnums.ServiceType;
import ru.kudinov.service.RequestService;
import ru.kudinov.service.ServRequestService;
import ru.kudinov.service.ServService;
import ru.kudinov.util.CookiesUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/service")
public class ServiceController {

    private final ServService servService;
    private final RequestService requestService;
    private final ServRequestService servRequestService;

    public ServiceController(ServService servService, RequestService requestService,
                             ServRequestService servRequestService) {
        this.servService = servService;
        this.requestService = requestService;
        this.servRequestService = servRequestService;
    }

    @GetMapping
    public String getServicePage(Model model) {

        List<ServiceType> serviceTypes = Arrays.stream(ServiceType.values()).toList();
        Map<String, List<Service>> services = new HashMap<>();

        for (ServiceType serviceType : serviceTypes) {
            services.put(serviceType.getValue(), servService.findByServiceType(serviceType));
        }

        model.addAttribute("services", services);
        return "service";
    }

    @GetMapping("{service}")
    public String signUpService(@PathVariable Service service, @AuthenticationPrincipal User authUser,
                                HttpServletResponse response) {

        if (authUser == null) {
            CookiesUtil.createServiceCookie(service, response);
        } else {
            Request request = requestService.getRequest(authUser);

            if (servRequestService.getServiceRequest(service, request) != null) {
                //TODO: добавить отображение ошибки
                //redirectAttributes.addFlashAttribute(service.getId() + "message", "Данная услуга уже добавлена");
                return "redirect:/service";
            }

            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setService(service);
            serviceRequest.setPrice(service.getPrice());
            serviceRequest.setRequest(request);

            servRequestService.saveServiceRequest(serviceRequest);
        }

        return "redirect:/service";
    }
}
