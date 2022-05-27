package ru.digital.services.sp.Event.Admin.Preview;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.EventRequests.EventAdminRequests;
import ru.digital.services.sp.API.EventRequests.EventRequests;
import ru.digital.services.sp.Utils.EventsUtils;
import ru.digital.services.sp.Dictionaries.Events.Admin.CancelCauses;
import ru.digital.services.sp.Dictionaries.Events.Admin.EventStatuses;
import ru.digital.services.sp.Dictionaries.Events.EventTypes;
import ru.digital.services.sp.Dictionaries.Events.UserEventTabs;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FilterEventsTests {

    //список событий на удаление после тестов
    public List<String> eventIdsToDelete = new ArrayList<>();

    //Идентификатор события с типом Мероприятие
    public static String eventId;

    //Идентификатор события с типом Проект
    public static String projectId;

    private static User user = Users.getUser("02022035663");

    @BeforeClass
    public void создание_разных_событий_для_проверки_фильтрации() {
        //Генерируем событие с типом Проект
        JSONObject projectBody = EventsUtils.generateCreateEventRequestBody();
        projectBody
                .put("type", EventTypes.PROJECT)
                .put("dateTimeStart",
                        OffsetDateTime.ofInstant(LocalDateTime.now().minusHours(1).toInstant(OffsetDateTime.now().getOffset()),
                                ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .put("orderForm", new JSONObject()
                        .put("emailReciveOrder", "")
                        .put("fields", new JSONArray()));
        Response response = EventAdminRequests.createEvent(projectBody.toString());

        projectId = response.jsonPath().getString("id");
        eventIdsToDelete.add(projectId);

        EventAdminRequests.changeEventStatus(projectId, EventStatuses.PUBLISHED);

        //Генерируем событие см типом Мероприятие
        JSONObject eventBody = EventsUtils.generateCreateEventRequestBody();
        eventBody.put("type", EventTypes.EVENT)
                .put("dateTimeStart",
                        OffsetDateTime.ofInstant(LocalDateTime.now().minusHours(1).toInstant(OffsetDateTime.now().getOffset()),
                                ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .put("orderForm", new JSONObject()
                        .put("emailReciveOrder", "")
                        .put("fields", new JSONArray()));
        response = EventAdminRequests.createEvent(eventBody.toString());
        eventId = response.jsonPath().getString("id");
        eventIdsToDelete.add(eventId);
        EventAdminRequests.changeEventStatus(eventId, EventStatuses.PUBLISHED);
    }

    @Test(testName = "CPO-Т380 Фильтрация по типу Мероприятия в разделе Опубликованные")
    public void фильтрация_по_типу_мероприятия_в_разделе_опубликованные() {
        //Фильтр по опубликованным мероприятиям
        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("status", EventStatuses.PUBLISHED)
                        .put("type", EventTypes.EVENT)).toString();

        Response eventList = EventAdminRequests.getEventList("", listFilter);

        Assert.assertTrue(eventList.jsonPath().getList("id").contains(eventId),
                "Не нашли значение в списке: " + eventId);
    }


    @Test(testName = "CPO-Т380 Фильтрация по типу Проект в разделе Опубликованные")
    public void фильтрация_по_типу_проект_в_разделе_опубликованные() {
        //Фильтр по опубликованным мероприятиям
        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("status", EventStatuses.PUBLISHED)
                        .put("type", EventTypes.PROJECT)).toString();

        Response eventList = EventAdminRequests.getEventList("", listFilter);

        Assert.assertTrue(eventList.jsonPath().getList("id").contains(projectId),
                "Не нашли значение в списке: " + projectId);
    }


    @Test(testName = "CPO-Т380 Фильтрация по типу ВСЕ в разделе Опубликованные")
    public void фильтрация_по_типу_все_в_разделе_опубликованные() {
        //Фильтр по опубликованным мероприятиям
        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("status", EventStatuses.PUBLISHED)).toString();

        Response eventList = EventAdminRequests.getEventList("", listFilter);

        Assert.assertTrue(eventList.jsonPath().getList("id").contains(eventId),
                "Не нашли значение в списке: : " + eventId);

        Assert.assertTrue(eventList.jsonPath().getList("id").contains(projectId),
                "Не нашли значение в списке: " + projectId);
    }

    @Test(testName = "CPO-Т389 Отображение событий мероприятий на вкладке Участвую")
    public void отображение_событий_мероприятий_на_вкладке_участвую() {

        JSONObject orderRequest = new JSONObject()
                .put("fields", new JSONArray())
                .put("isConsentPersonalData", "true");

        EventRequests.createOrder(user, eventId, orderRequest.toString());

        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("tabs", UserEventTabs.PARTICIPATE)
                        .put("type", EventTypes.EVENT))
                .toString();

        Response eventList = EventRequests.getUserEventList(user, "", listFilter);

        Assert.assertTrue(eventList.jsonPath().getList("id").contains(eventId), "Не найдено событие типа Мероприятие");
    }

    @Test(testName = "CPO-Т389 Отображение событий проектов на вкладке Участвую")
    public void отображение_событий_проектов_на_вкладке_участвую() {

        JSONObject orderRequest = new JSONObject()
                .put("fields", new JSONArray())
                .put("isConsentPersonalData", "true");

        EventRequests.createOrder(user, projectId, orderRequest.toString());


        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("tabs", UserEventTabs.PARTICIPATE)
                        .put("type", EventTypes.PROJECT))
                .toString();

        Response eventList = EventRequests.getUserEventList(user, "", listFilter);

        Assert.assertTrue(eventList.jsonPath().getList("id").contains(projectId), "Не найдено событие типа Проект");
    }

    @AfterClass
    public void remove_events()
    {
        for (String eventId: eventIdsToDelete) {
            EventAdminRequests.cancelEvent(eventId, CancelCauses.CANCELED, "");
            EventAdminRequests.deleteEvent(eventId);
        }
    }
}
