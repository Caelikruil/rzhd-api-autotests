package ru.digital.services.sp.Dictionaries.housingpolicy;

public enum ApplicationRequestsStatuses {
    SENDED("1"),
    UNDER_CONSIDERATION("2"),
    APPROVED("3"),
    REJECTED("4");

    private String id;

    ApplicationRequestsStatuses(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
