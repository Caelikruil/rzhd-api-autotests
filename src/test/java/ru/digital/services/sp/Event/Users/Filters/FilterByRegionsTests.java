package ru.digital.services.sp.Event.Users.Filters;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.EventRequests.EventAdminRequests;
import ru.digital.services.sp.API.EventRequests.EventRequests;
import ru.digital.services.sp.Utils.EventsUtils;
import ru.digital.services.sp.Dictionaries.Events.Admin.CancelCauses;
import ru.digital.services.sp.Dictionaries.Events.EventFormatTypes;
import ru.digital.services.sp.Dictionaries.Events.Admin.EventStatuses;
import ru.digital.services.sp.Dictionaries.Events.UserEventGroups;
import ru.digital.services.sp.Dictionaries.Events.UserEventTabs;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.util.ArrayList;
import java.util.List;

public class FilterByRegionsTests {

    //Идентификатор события с форматом проведения офлайн и регионом Башкирия
    public static String bashkiriyaId;
    //Идентификатор события с форматом проведения офлайн и регионом Оренбургская обл
    public static String orenbergId;

    public List<String> eventIdsToDelete = new ArrayList<>();
    private static User user = Users.getUser("02022035663");

    @BeforeClass
    public void создание_разных_событий_для_проверки_фильтрации() {

        //Генерируем событие с форматом проведения офлайн и регионом Башкирия
        JSONObject oooBody = EventsUtils.generateCreateEventRequestBody();
        oooBody.put("format", EventFormatTypes.OFFLINE);
        JSONObject location = (JSONObject) oooBody.get("location");
        location.put("regionCode", "02");
        Response response = EventAdminRequests.createEvent(oooBody.toString());

        bashkiriyaId = response.jsonPath().getString("id");
        eventIdsToDelete.add(bashkiriyaId);
        EventAdminRequests.changeEventStatus(bashkiriyaId, EventStatuses.PUBLISHED);

        //Генерируем событие с форматом проведения офлайн и регионом Оренбургская обл
        JSONObject orenBody = EventsUtils.generateCreateEventRequestBody();
        orenBody.put("format", EventFormatTypes.OFFLINE);
        location = (JSONObject) orenBody.get("location");
        location.put("regionCode", "56");
        response = EventAdminRequests.createEvent(orenBody.toString());

        orenbergId = response.jsonPath().getString("id");
        eventIdsToDelete.add(orenbergId);
        EventAdminRequests.changeEventStatus(orenbergId, EventStatuses.PUBLISHED);

        EventRequests.setUserRegion(user, "56");
    }

    @Test(testName = "CPO-Т384 Отображение Событий с фильтрацией по регионам")
    public void отображение_событий_с_фильтрацией_по_регионам() {
        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("tabs", UserEventTabs.ACTIVE)
                        .put("groups", UserEventGroups.LATEST_PUBLISHED))
                .toString();

        Response eventList = EventRequests.getUserEventList(user, "", listFilter);

        Assert.assertTrue(eventList.jsonPath().getList("id").contains(orenbergId), "Не найден Оренбургский офлайн ивент");
        Assert.assertFalse(eventList.jsonPath().getList("id").contains(bashkiriyaId), "Найден Башкирский офлайн ивент");

        //Переключаем регион на Башкортостан
        EventRequests.setUserRegion(user, "02");

        eventList = EventRequests.getUserEventList(user, "", listFilter);

        Assert.assertFalse(eventList.jsonPath().getList("id").contains(orenbergId), "Найден Оренбургский офлайн ивент");
        Assert.assertTrue(eventList.jsonPath().getList("id").contains(bashkiriyaId), "Не найден Башкирский офлайн ивент");
    }

    @AfterClass
    public void remove_events() {
        for (String eventId : eventIdsToDelete) {
            EventAdminRequests.cancelEvent(eventId, CancelCauses.CANCELED, "");
            EventAdminRequests.deleteEvent(eventId);
        }
    }
}
