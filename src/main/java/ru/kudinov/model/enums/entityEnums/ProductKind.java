package ru.kudinov.model.enums.entityEnums;

public enum ProductKind {

    SERVICE("service_", "^service_\\d+"),
    DETAIL("detail_", "^detail_\\d+");

    private final String PRODUCT_COOKIE_NAME;
    private final String PRODUCT_COOKIE_REGEX;

    ProductKind(String PRODUCT_COOKIE_NAME, String PRODUCT_COOKIE_REGEX) {
        this.PRODUCT_COOKIE_NAME = PRODUCT_COOKIE_NAME;
        this.PRODUCT_COOKIE_REGEX = PRODUCT_COOKIE_REGEX;
    }

    public String getPRODUCT_COOKIE_NAME() {
        return PRODUCT_COOKIE_NAME;
    }

    public String getPRODUCT_COOKIE_REGEX() {
        return PRODUCT_COOKIE_REGEX;
    }


}
