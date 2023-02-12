package ru.kudinov.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import ru.kudinov.model.enums.sortEnums.SortableType;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Component
public class PaginationUtil {

    public <T> void createPageableAndSort(Model model, String entityName, Page<T> page, HttpServletRequest request,
                                          SortableType sortableType) {

        addPaginationInModel(model, entityName + "Page", page, request);

        model.addAttribute("current" + entityName.substring(0, 1).toUpperCase() + entityName.substring(1) + "SortType", sortableType);
        model.addAttribute(entityName + "SortTypes", sortableType.getValues());
    }

    public Pageable createPageable(String pageNumber, SortableType sortableType) {

        return PageRequest.of(Integer.parseInt(pageNumber), 5, sortableType.getSortDirection(), sortableType.getEntitySortField());
    }

    public <T> void addPaginationInModel(Model model, String modelPageName, Page<T> page, HttpServletRequest request) {

        model.addAttribute("pagesCount", getPagesCount(page));
        model.addAttribute("url", getUrlForPagination(request));
        model.addAttribute(modelPageName, page);
    }

    public <T> List<Integer> getPagesCount(Page<T> page) {

        int totalPages = page.getTotalPages();
        int currentPageNumber = page.getNumber() + 1;
        List<Integer> pagesCount = new LinkedList<>();

        if (totalPages < 8) {
            pagesCount.addAll(IntStream.rangeClosed(1, totalPages)
                    .boxed().toList());
        } else {
            List<Integer> head = (currentPageNumber > 4) ? Arrays.asList(1, -1) : Arrays.asList(1, 2, 3);
            List<Integer> tail = (currentPageNumber < totalPages - 3) ?
                    Arrays.asList(-1, totalPages) : Arrays.asList(totalPages - 2, totalPages - 1, totalPages);
            List<Integer> bodyBefore = (currentPageNumber > 4 && currentPageNumber < totalPages - 1) ?
                    Arrays.asList(currentPageNumber - 2, currentPageNumber - 1) : List.of();
            List<Integer> bodyAfter = (currentPageNumber > 2 && currentPageNumber < totalPages - 3) ?
                    Arrays.asList(currentPageNumber + 1, currentPageNumber + 2) : List.of();

            pagesCount.addAll(head);
            pagesCount.addAll(bodyBefore);
            if (currentPageNumber > 3 && currentPageNumber < totalPages - 2) pagesCount.add(currentPageNumber);
            pagesCount.addAll(bodyAfter);
            pagesCount.addAll(tail);
        }

        return pagesCount;
    }

    public String getUrlForPagination(HttpServletRequest request) {

        String url = request.getRequestURI() + "?";
        if (request.getQueryString() != null) {
            url += request.getQueryString();
            Pattern pattern = Pattern.compile("page=\\d+&?");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                url = url.replace(matcher.group(), "");
                if (!request.getQueryString().replace(matcher.group(), "").equals("") &&
                        url.charAt(url.length() - 1) != '&') {
                    url += '&';
                }
            } else {
                url += '&';
            }
        }
        return url;
    }
}
