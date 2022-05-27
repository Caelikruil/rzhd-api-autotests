package ru.digital.services.sp.Event.Users.Favorite;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.EventRequests.EventAdminRequests;
import ru.digital.services.sp.API.EventRequests.EventRequests;
import ru.digital.services.sp.Utils.EventsUtils;
import ru.digital.services.sp.Dictionaries.Events.Admin.CancelCauses;
import ru.digital.services.sp.Dictionaries.Events.Admin.EventStatuses;
import ru.digital.services.sp.Dictionaries.Events.UserEventTabs;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

public class FavoriteEventTests {

    public static String eventId;
    public static User user = Users.getUser("02022035663");

    @BeforeTest
    public void предусловия_тестов_избранных_событий() {
        JSONObject requestBody = EventsUtils.generateCreateEventRequestBody();
        Response response = EventAdminRequests.createEvent(requestBody.toString());

        eventId = response.jsonPath().getString("id");
        EventAdminRequests.changeEventStatus(eventId, EventStatuses.PUBLISHED);
    }

    @Test(testName = "T390 - Добавление События в список избранных",
            priority = 1)
    public void добавление_события_в_список_избранных() throws InterruptedException {

        EventRequests.makeEventFavorite(user, eventId);
        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("tabs", UserEventTabs.FAVORITE))
                .toString();

        Response eventList = EventRequests.getUserEventList(user, "", listFilter);

        Assert.assertTrue(eventList.jsonPath().getList("id").contains(eventId), "Не найдено избранное событие на вкладке Избранные");
    }

    @Test(testName = "T390 - Добавление События в список избранных",
            priority = 2, dependsOnMethods = ("добавление_события_в_список_избранных"))
    public void удаление_события_из_избранных() throws InterruptedException {
        EventRequests.removeEventFromFavorite(user, eventId);
        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("tabs", UserEventTabs.FAVORITE))
                .toString();

        Response eventList = EventRequests.getUserEventList(user, "", listFilter);

        Assert.assertFalse(eventList.jsonPath().getList("id").contains(eventId), "Найдено не избранное событие на вкладке Избранные");
    }

    @AfterClass
    public void remove_events() {
        EventAdminRequests.cancelEvent(eventId, CancelCauses.CANCELED, "");
        EventAdminRequests.deleteEvent(eventId);
    }
}
