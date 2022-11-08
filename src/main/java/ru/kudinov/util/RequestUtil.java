package ru.kudinov.util;

import ru.kudinov.service.DetailRequestService;
import ru.kudinov.service.ServRequestService;


public class RequestUtil {

    private final DetailRequestService detailRequestService;
    private final ServRequestService servRequestService;

    public RequestUtil(DetailRequestService detailRequestService, ServRequestService servRequestService) {
        this.detailRequestService = detailRequestService;
        this.servRequestService = servRequestService;
    }


}
