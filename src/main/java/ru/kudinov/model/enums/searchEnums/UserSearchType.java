package ru.kudinov.model.enums.searchEnums;

public enum UserSearchType implements SearchableType {
    ID("id", "По ID"),
    USERNAME("username", "По логину"),
    NAME("name", "По имени"),
    SURNAME("surname", "По фамилии"),
    PATRONYMIC("patronymic", "По отчеству"),
    SNP("surname name patronymic", "По ФИО"),
    PHONE_NUMBER("phoneNumber", "По номеру телефона"),
    EMAIL("email", "По email");


    private final String entitySearchField;
    private final String searchName;

    UserSearchType(String entitySearchField, String searchName) {
        this.entitySearchField = entitySearchField;
        this.searchName = searchName;
    }

    @Override
    public String getEntitySearchField() {
        return entitySearchField;
    }

    @Override
    public String getSearchName() {
        return searchName;
    }

    @Override
    public SearchableType[] getValues() {
        return UserSearchType.values();
    }


}
