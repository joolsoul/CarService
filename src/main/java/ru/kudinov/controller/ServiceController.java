package ru.kudinov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kudinov.model.Service;
import ru.kudinov.model.enums.ServiceType;
import ru.kudinov.service.ServiceService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/service")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public String getServices(Model model) {

        List<ServiceType> serviceTypes = Arrays.stream(ServiceType.values()).toList();
        Map<String, List<Service>> services = new HashMap<>();

        for (ServiceType serviceType : serviceTypes) {
            services.put(serviceType.getValue(), serviceService.findByServiceType(serviceType));
        }

        model.addAttribute("services", services);
        return "service";
    }

    @GetMapping("{service}")
    public String signUpService(@PathVariable String service) {
        return "";
    }
}
