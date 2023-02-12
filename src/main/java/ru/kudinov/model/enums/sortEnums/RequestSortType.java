package ru.kudinov.model.enums.sortEnums;

import org.springframework.data.domain.Sort;

public enum RequestSortType implements SortableType {

    ORDER_DATE_DESC("orderDate", "Сначала новые", Sort.Direction.DESC),
    ORDER_DATE_ASC("orderDate", "Сначала старые", Sort.Direction.ASC),
    COST_DESC("cost", "Сначала большая сумма заказа", Sort.Direction.DESC),
    COST_ASC("cost", "Сначала меньшая сумма заказа", Sort.Direction.ASC);

    private final String entitySortField;
    private final String sortName;
    private final Sort.Direction sortDirection;

    RequestSortType(String entitySortField, String sortName, Sort.Direction sortDirection) {
        this.entitySortField = entitySortField;
        this.sortName = sortName;
        this.sortDirection = sortDirection;
    }

    @Override
    public String getEntitySortField() {
        return entitySortField;
    }

    @Override
    public String getSortName() {
        return sortName;
    }

    @Override
    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    @Override
    public SortableType[] getValues() {
        return RequestSortType.values();
    }
}
