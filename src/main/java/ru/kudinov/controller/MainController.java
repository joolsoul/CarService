package ru.kudinov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kudinov.model.User;
import ru.kudinov.util.CartUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {

    private final CartUtil cartUtil;

    public MainController(CartUtil cartUtil) {
        this.cartUtil = cartUtil;
    }

    //TODO сделать корзину offcanvas
    @GetMapping("/")
    public String greeting(Model model, @AuthenticationPrincipal User user,
                           HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        cartUtil.createUserCart(model, user, httpRequest, httpResponse);

        return "index";
    }
}