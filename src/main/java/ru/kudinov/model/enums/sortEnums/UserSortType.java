package ru.kudinov.model.enums.sortEnums;

import org.springframework.data.domain.Sort;

public enum UserSortType implements SortableType {

    ID_DESC("id", "Сначала новые", Sort.Direction.DESC),
    ID_ASC("id", "Сначала старые", Sort.Direction.ASC);


    private final String entitySortField;
    private final String sortName;
    private final Sort.Direction sortDirection;

    UserSortType(String entitySortField, String sortName, Sort.Direction sortDirection) {
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
