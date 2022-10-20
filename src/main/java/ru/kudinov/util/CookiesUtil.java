package ru.kudinov.util;

import ru.kudinov.model.Producible;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookiesUtil {

    public static void createCookies(Producible producible, String product, HttpServletResponse response, String productCount) {
        Cookie productCountCookie;
        if (productCount == null) {
            productCountCookie = new Cookie(product + "Count", "1");

        } else {
            int detailCountCookieValue = Integer.parseInt(productCount);
            productCountCookie = new Cookie(product + "Count", Integer.toString(detailCountCookieValue + 1));
        }
        CookiesUtil.createCookie(producible, product, response, productCountCookie);
    }

    private static void createCookie(Producible producible, String productType, HttpServletResponse response, Cookie productCountCookie) {

        productCountCookie.setMaxAge(24 * 60 * 60);
        Cookie productCookie = new Cookie(productType + productCountCookie.getValue(), producible.getId().toString());
        productCookie.setMaxAge(24 * 60 * 60);

        response.addCookie(productCountCookie);
        response.addCookie(productCookie);
    }
}
