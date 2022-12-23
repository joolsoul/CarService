package ru.kudinov.model.enums.entityEnums;

public enum ServOrgWorkSchedule {
    MND_FRD("ПН-ПТ", "09:00-20:00"),
    DAILY("Ежедневно", "09:00-19:00");

    private final String weekdays;

    private final String time;

    ServOrgWorkSchedule(String weekdays, String time) {
        this.weekdays = weekdays;
        this.time = time;
    }

    public String getWeekday() {
        return weekdays;
    }

    public String getTime() {
        return time;
    }
}
