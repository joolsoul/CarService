package ru.kudinov.util;

import ru.kudinov.model.Detail;
import ru.kudinov.model.Service;
import ru.kudinov.model.enums.entityEnums.ProductKind;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CookiesUtil {

    private final static int COOKIES_TIMEOUT = 60 * 3;
    private final static String COOKIES_PATH = "/";

    public static void createServiceCookie(Service service, HttpServletResponse response) {
        createCookie(response, service.getPRODUCT_KIND().getPRODUCT_COOKIE_NAME() + service.getId(), "");
    }

    public static void createDetailCookie(Detail detail, int quantity, HttpServletResponse response, HttpServletRequest request) {

        Map<String, String> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

        String cookieName = detail.getPRODUCT_KIND().getPRODUCT_COOKIE_NAME() + detail.getId();

        if (cookies.containsKey(cookieName)) {
            int currentQuantity = Integer.parseInt(cookies.get(cookieName));

            if (currentQuantity + quantity > detail.getQuantity())
                createCookie(response, cookieName, Integer.toString(detail.getQuantity()));

            else createCookie(response, cookieName, Integer.toString(currentQuantity + quantity));
        } else createCookie(response, cookieName, Integer.toString(quantity));
    }

    public static void createCookie(HttpServletResponse response, String cookieKey, String cookieValue) {
        Cookie cookie = new Cookie(cookieKey, cookieValue);
        cookie.setMaxAge(COOKIES_TIMEOUT);
        cookie.setPath(COOKIES_PATH);
        response.addCookie(cookie);
    }


    public static void clearProductCookies(HttpServletRequest httpRequest, HttpServletResponse response) {
        Map<String, String> cookies = Arrays.stream(httpRequest.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

        for (ProductKind productKind : ProductKind.values()) {
            for (Map.Entry<String, String> cookie : cookies.entrySet()) {
                if (Pattern.matches(productKind.getPRODUCT_COOKIE_REGEX(), cookie.getKey())) {
                    deleteCookie(response, cookie.getKey());
                }
            }
        }
    }

    public static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie newCookie = new Cookie(cookieName, null);
        newCookie.setMaxAge(0);
        newCookie.setPath(COOKIES_PATH);
        response.addCookie(newCookie);
    }

    public static void clearCookies(HttpServletRequest httpRequest, HttpServletResponse response, List<String> cookieNames) {
        Set<String> cookieNamesSet = new HashSet<>(cookieNames);
        List<Cookie> cookies = List.of(httpRequest.getCookies());
        for (Cookie cookie : cookies) {
            if (cookieNamesSet.contains(cookie.getName())) {
                deleteCookie(response, cookie.getName());
            }
        }
    }

    public static void clearAllCookies(HttpServletRequest httpRequest, HttpServletResponse response, List<String> exceptions) {
        Set<String> exceptionsCookieNamesSet = new HashSet<>(exceptions);
        List<Cookie> cookies = List.of(httpRequest.getCookies());
        for (Cookie cookie : cookies) {
            if (!exceptionsCookieNamesSet.contains(cookie.getName())) {
                deleteCookie(response, cookie.getName());
            }
        }
    }
}
