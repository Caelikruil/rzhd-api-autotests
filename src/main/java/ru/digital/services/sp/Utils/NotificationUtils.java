package ru.digital.services.sp.Utils;

import ru.digital.services.sp.API.NotificationRequests.NotificationRequests;
import ru.digital.services.sp.Users.User;

import java.util.List;

public class NotificationUtils {

    private static NotificationRequests httpRequestSender = new NotificationRequests();

    public static void readAllUserNotifications(User user)
    {
        int unreadCount = Integer.parseInt(httpRequestSender.getBearerRequestWithCreds(user)
                .get(NotificationRequests.getUnreadCountUrl)
                        .then().log().all().extract().response()
                .body().jsonPath().getString("count"));

        //Если непрочтенных нотификаций нет, то пропускаем
        if (unreadCount == 0)
            return;

        List<String> unreadNotificationIds = httpRequestSender.getBearerRequestWithCreds(user)
                .queryParam("mode", "unread")
                .queryParam("pageSize", unreadCount)
                .get(NotificationRequests.getNotificationsUrl)
                .body().jsonPath().getList("data.id");

        httpRequestSender.getBearerRequestWithCreds(user)
                .queryParam("ids[]", unreadNotificationIds)
                .get(NotificationRequests.readNotificationsUrl);
    }
}
