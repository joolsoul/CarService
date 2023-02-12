package ru.kudinov.model.enums.sortEnums;

import org.springframework.data.domain.Sort;

public interface SortableType {
    String getEntitySortField();

    String getSortName();

    Sort.Direction getSortDirection();

    SortableType[] getValues();
}
