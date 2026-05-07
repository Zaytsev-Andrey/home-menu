package ru.homemenu.logging.service.event;

public final class ServiceLogEvent {

    private ServiceLogEvent() {}

    public static final String ENTRY = "service.method.entry";
    public static final String EXIT = "service.method.exit";
    public static final String ERROR = "service.method.error";

}
