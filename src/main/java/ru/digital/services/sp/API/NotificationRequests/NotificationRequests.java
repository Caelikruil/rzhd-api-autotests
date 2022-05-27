package ru.digital.services.sp.API.NotificationRequests;

import org.apache.http.annotation.Obsolete;
import ru.digital.services.sp.API.NewBaseApi;

public class NotificationRequests extends NewBaseApi {

    public static String getNotificationsUrl = "";
    public static String getUnreadCountUrl = "count";
    public static String readNotificationsUrl = "read";
    public static String readNotificationUrl = "read/";

    //Базовый путь сервиса
    private final String basePath = "api/notification/";

    protected String getBasePath() {
        return this.basePath;
    }
}

