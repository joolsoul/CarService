package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kudinov.model.Detail;
import ru.kudinov.model.DetailRequest;
import ru.kudinov.model.Request;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.DetailType;
import ru.kudinov.service.DetailRequestService;
import ru.kudinov.service.DetailService;
import ru.kudinov.service.RequestService;
import ru.kudinov.util.CookiesUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shop")
public class ShopController {

    private final DetailService detailService;
    private final RequestService requestService;
    private final DetailRequestService detailRequestService;

    public ShopController(DetailService detailService, RequestService requestService, DetailRequestService detailRequestService) {
        this.detailService = detailService;
        this.requestService = requestService;
        this.detailRequestService = detailRequestService;
    }

    @GetMapping
    public String getShopPage(Model model, @RequestParam(required = false) DetailType filterDetailType,
                              HttpServletRequest request) {

        List<DetailType> detailTypes = Arrays.stream(DetailType.values()).toList();
        model.addAttribute("detailTypes", detailTypes);

        Map<String, List<Detail>> details = new HashMap<>();
        HttpSession session = request.getSession();

        if (filterDetailType != null) {
            details.put(filterDetailType.getValue(), detailService.findByDetailType(filterDetailType));
            session.setAttribute("filterDetailType", filterDetailType);
        } else {
            session.removeAttribute("filterDetailType");
            for (DetailType detailType : detailTypes) {
                details.put(detailType.getValue(), detailService.findByDetailType(detailType));
            }
        }

        model.addAttribute("details", details);
        return "shop";
    }

    @GetMapping("{detail}")
    public String signUpDetail(@PathVariable Detail detail, @AuthenticationPrincipal User authUser,
                               HttpServletResponse httpResponse,
                               @CookieValue(value = "detailCount", required = false) String detailCount,
                               HttpServletRequest httpRequest) {

        if (authUser == null) {
            CookiesUtil.createCookies(detail, "detail", httpResponse, detailCount);
        } else {
            Request request = requestService.getRequest(authUser);

            DetailRequest detailRequest = detailRequestService.getDetailRequest(detail, request);
            if (detailRequest != null) {
                //TODO: сделать выбор количества
                detailRequest.setQuantity(detailRequest.getQuantity() + 1);
                detailRequest.setPrice(detailRequest.getPrice() + detail.getPrice());
            } else {

                detailRequest = new DetailRequest();
                detailRequest.setDetail(detail);
                detailRequest.setPrice(detail.getPrice());
                detailRequest.setRequest(request);
                //TODO: сделать выбор количества
                detailRequest.setQuantity(1);
            }

            detailRequestService.saveDetailRequest(detailRequest);
        }

        HttpSession session = httpRequest.getSession();
        DetailType filterDetailType = (DetailType) session.getAttribute("filterDetailType");
        if (filterDetailType != null) {
            return "redirect:/shop?filterDetailType=" + filterDetailType.name();
        }
        return "redirect:/shop";
    }


}
