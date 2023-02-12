package ru.kudinov.util;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import ru.kudinov.model.*;
import ru.kudinov.model.enums.entityEnums.ProductKind;
import ru.kudinov.model.enums.entityEnums.RequestStatus;
import ru.kudinov.model.interfaces.Producible;
import ru.kudinov.model.interfaces.ProducibleRequest;
import ru.kudinov.service.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CartUtil {

    private final ServService servService;
    private final DetailService detailService;
    private final RequestService requestService;
    private final DetailRequestService detailRequestService;
    private final ServRequestService servRequestService;
    private final ServOrganizationService servOrganizationService;
    private final CarService carService;
    private final EmployeeService employeeService;

    private final Pattern ID_PATTERN = Pattern.compile("\\d+");

    public CartUtil(ServService servService, DetailService detailService, RequestService requestService,
                    DetailRequestService detailRequestService, ServRequestService servRequestService,
                    ServOrganizationService servOrganizationService, CarService carService,
                    EmployeeService employeeService) {

        this.servService = servService;
        this.detailService = detailService;
        this.requestService = requestService;
        this.detailRequestService = detailRequestService;
        this.servRequestService = servRequestService;
        this.servOrganizationService = servOrganizationService;
        this.carService = carService;
        this.employeeService = employeeService;
    }

    public void createUserCart(Model model, User user,
                               HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        int basketProductCounter = 0;
        List<ServiceRequest> cookiesServiceRequests;
        List<DetailRequest> cookiesDetailRequests;

        cookiesServiceRequests = createRequestsFromCookies(httpRequest, ProductKind.SERVICE);

        cookiesDetailRequests = createRequestsFromCookies(httpRequest, ProductKind.DETAIL);

        basketProductCounter = cookiesServiceRequests.size() + cookiesDetailRequests.size();

        model.addAttribute("detailRequests", cookiesDetailRequests);
        model.addAttribute("serviceRequests", cookiesServiceRequests);

        Request nonAuthenticationRequest = new Request();

        cookiesDetailRequests.forEach(detailRequest -> detailRequest.setRequest(nonAuthenticationRequest));
        cookiesServiceRequests.forEach(serviceRequest -> serviceRequest.setRequest(nonAuthenticationRequest));

        calculateNonAuthenticationRequestCost(nonAuthenticationRequest, cookiesDetailRequests, cookiesServiceRequests);
        model.addAttribute("request", nonAuthenticationRequest);

        if (user != null) {

            Request userRequest = requestService.getRequest(user);

            List<ServiceRequest> serviceRequests = servRequestService.getServiceRequestsByRequest(userRequest);
            Set<Long> servicesId = serviceRequests.stream().map(s -> s.getService().getId()).collect(Collectors.toSet());

            List<DetailRequest> detailRequests = detailRequestService.getDetailRequestsByRequest(userRequest);
            Set<Long> detailsId = detailRequests.stream().map(s -> s.getDetail().getId()).collect(Collectors.toSet());

            for (ServiceRequest serviceRequest : cookiesServiceRequests) {
                if (!servicesId.contains(serviceRequest.getService().getId())) {

                    serviceRequests.add(serviceRequest);
                    serviceRequest.setRequest(userRequest);
                    servRequestService.saveServiceRequest(serviceRequest);

                }
            }

            for (DetailRequest cookieDetailRequest : cookiesDetailRequests) {
                if (!detailsId.contains(cookieDetailRequest.getDetail().getId())) {

                    detailRequests.add(cookieDetailRequest);
                    cookieDetailRequest.setRequest(userRequest);
                    detailRequestService.saveDetailRequest(cookieDetailRequest);

                } else {

                    DetailRequest existingDetailRequest = detailRequests.stream().filter(dr ->
                                    cookieDetailRequest.getDetail().getId().equals(dr.getDetail().getId()))
                            .findFirst().orElse(null);

                    assert existingDetailRequest != null;
                    if (existingDetailRequest.getQuantity() + cookieDetailRequest.getQuantity() >
                            existingDetailRequest.getDetail().getQuantity()) {

                        existingDetailRequest.setQuantity(existingDetailRequest.getDetail().getQuantity());

                    } else {

                        existingDetailRequest.setQuantity(existingDetailRequest.getQuantity() + cookieDetailRequest.getQuantity());

                    }

                    existingDetailRequest.setPrice(existingDetailRequest.getQuantity() *
                            existingDetailRequest.getDetail().getPrice());
                    detailRequestService.saveDetailRequest(existingDetailRequest);

                }
            }

            calculateRequestCost(userRequest);
            requestService.updateRequest(userRequest);

            basketProductCounter = serviceRequests.size() + detailRequests.size();

            CookiesUtil.clearProductCookies(httpRequest, httpResponse);

            model.addAttribute("detailRequests", detailRequests);
            model.addAttribute("serviceRequests", serviceRequests);
            model.addAttribute("user", user);
            model.addAttribute("request", userRequest);
        }

        model.addAttribute("cartProductCounter", basketProductCounter);

    }

    private <C extends ProducibleRequest> List<C> createRequestsFromCookies(HttpServletRequest request, ProductKind productKind) {

        return switch (productKind) {
            case SERVICE -> createServiceRequestFromCookie(request, ProductKind.SERVICE.getPRODUCT_COOKIE_REGEX());
            case DETAIL -> createDetailRequestFromCookie(request, ProductKind.DETAIL.getPRODUCT_COOKIE_REGEX());
        };
    }

    private <C extends ProducibleRequest> List<C> createServiceRequestFromCookie(HttpServletRequest request,
                                                                                 String pattern) {
        List<C> serviceRequests = new LinkedList<>();

        List<Cookie> cookies = new LinkedList<>();
        if(request.getCookies() != null) cookies = List.of(request.getCookies());

        for (Cookie cookie : cookies) {
            if (Pattern.matches(pattern, cookie.getName())) {

                Matcher matcher = ID_PATTERN.matcher(cookie.getName());

                if (matcher.find()) {

                    String id = matcher.group();
                    Service service = servService.findById(Long.parseLong(id));

                    if (service != null) {
                        ServiceRequest serviceRequest = new ServiceRequest();
                        serviceRequest.setProduct(service);
                        serviceRequest.setPrice(service.getPrice());

                        serviceRequests.add((C) serviceRequest);
                    }
                }
            }
        }
        return serviceRequests;
    }

    private <C extends ProducibleRequest> List<C> createDetailRequestFromCookie(HttpServletRequest request,
                                                                                String pattern) {
        List<C> detailRequests = new LinkedList<>();
        List<Cookie> cookies = new LinkedList<>();
        if(request.getCookies() != null) cookies = List.of(request.getCookies());

        for (Cookie cookie : cookies) {
            if (Pattern.matches(pattern, cookie.getName())) {

                Matcher matcher = ID_PATTERN.matcher(cookie.getName());

                if (matcher.find()) {
                    String detailId = matcher.group();
                    Detail detail = detailService.findById(Long.parseLong(detailId));

                    if (detail != null) {
                        DetailRequest detailRequest = new DetailRequest();
                        detailRequest.setProduct(detail);
                        detailRequest.setPrice(detail.getPrice() * Integer.parseInt(cookie.getValue()));
                        detailRequest.setQuantity(Integer.valueOf(cookie.getValue()));

                        detailRequests.add((C) detailRequest);
                    }
                }
            }
        }
        return detailRequests;
    }

    public void removeProducibleWithAuthUser(ProducibleRequest producibleRequest) {

        if (producibleRequest instanceof DetailRequest) {
            detailRequestService.removeDetailRequest((DetailRequest) producibleRequest);
        }
        if (producibleRequest instanceof ServiceRequest) {
            servRequestService.removeServiceRequest((ServiceRequest) producibleRequest);
        }
    }

    public void confirmRequest(User user, Map<String, String> form, Car selectCar, ServiceOrganization selectServiceOrganization) {

        Request request = requestService.getRequest(user);
        request.setRequestStatus(RequestStatus.CONFIRMED);
        request.setCar(selectCar);
        request.setServiceOrganization(selectServiceOrganization);
        request.setOrderDate(new Date());

        for (Map.Entry<String, String> formElement : form.entrySet()) {

            if (Pattern.matches(ProductKind.DETAIL.getPRODUCT_COOKIE_REGEX(), formElement.getKey())) {
                Matcher matcher = ID_PATTERN.matcher(formElement.getKey());

                if (matcher.find()) {
                    String detailId = matcher.group();
                    Detail detail = detailService.findById(Long.parseLong(detailId));

                    if (detail != null) {
                        DetailRequest detailRequest = detailRequestService.getDetailRequest(detail, request);
                        if (detailRequest != null) {
                            detailRequest.setQuantity(Integer.valueOf(formElement.getValue()));
                            detailRequestService.saveDetailRequest(detailRequest);
                            detail.setQuantity(detail.getQuantity() - detailRequest.getQuantity());
                            detailService.updateDetail(detail);
                        }
                    }
                }
            }
            if (Pattern.matches(ProductKind.SERVICE.getPRODUCT_COOKIE_REGEX(), formElement.getKey())) {
                Matcher matcher = ID_PATTERN.matcher(formElement.getKey());

                if (matcher.find()) {
                    String serviceId = matcher.group();
                    Service service = servService.findById(Long.parseLong(serviceId));

                    if (service != null) {
                        ServiceRequest serviceRequest = servRequestService.getServiceRequest(service, request);
                        if (serviceRequest != null) {
                            Employee employee = employeeService.findById(Long.valueOf(formElement.getValue()));
                            serviceRequest.setEmployee(employee);
                            servRequestService.saveServiceRequest(serviceRequest);
                        }
                    }
                }
            }

        }
        requestService.updateRequest(request);
    }

    public void removeProducibleWithNonAuthUser(Producible producible, HttpServletResponse httpResponse) {
        String cookieName = producible.getPRODUCT_KIND().getPRODUCT_COOKIE_NAME() + producible.getId();
        CookiesUtil.deleteCookie(httpResponse, cookieName);
    }

    private void calculateRequestCost(Request request) {

        List<DetailRequest> detailRequests = detailRequestService.getDetailRequestsByRequest(request);
        List<ServiceRequest> serviceRequests = servRequestService.getServiceRequestsByRequest(request);

        calculateNonAuthenticationRequestCost(request, detailRequests, serviceRequests);
    }

    private void calculateNonAuthenticationRequestCost(Request request, List<DetailRequest> detailRequests,
                                                       List<ServiceRequest> serviceRequests) {

        request.setCost(detailRequests.stream().mapToDouble(DetailRequest::getPrice).sum() +
                serviceRequests.stream().mapToDouble(ServiceRequest::getPrice).sum());
    }

    public void returnDetails(Request request) {
        Set<DetailRequest> detailRequests = request.getDetailRequests();

        for (DetailRequest detailRequest : detailRequests) {
            Detail detail = detailRequest.getDetail();
            detail.setQuantity(detail.getQuantity() + detailRequest.getQuantity());
            detailService.updateDetail(detail);
        }

    }
}
