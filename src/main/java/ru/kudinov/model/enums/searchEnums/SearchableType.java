package ru.kudinov.model.enums.searchEnums;

public interface SearchableType {
    String getEntitySearchField();

    String getSearchName();

    SearchableType[] getValues();
}
