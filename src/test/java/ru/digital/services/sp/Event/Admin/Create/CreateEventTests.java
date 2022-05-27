package ru.digital.services.sp.Event.Admin.Create;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.EventRequests.EventAdminRequests;
import ru.digital.services.sp.Utils.EventsUtils;
import ru.digital.services.sp.Dictionaries.Events.*;
import ru.digital.services.sp.Dictionaries.Events.Admin.CancelCauses;
import ru.digital.services.sp.Dictionaries.Events.Admin.EventCoverImageTypes;
import ru.digital.services.sp.Dictionaries.Events.Admin.EventStatuses;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.Utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEventTests {

    //список событий на удаление после тестов
    public List<String> eventIdsToDelete = new ArrayList<>();

    //Список параметров для проверки метода создания события
    @DataProvider(name = "форматы событий и уникальные поля")
    public Object[][] createWithMinimumFieldsParams() {
        return new Object[][]{
                //Онлайн мероприятие
                {
                        EventFormatTypes.ONLINE,
                        new HashMap<String, String>() {{
                            put("link", "\"https://ya.ru\"");
                        }}
                },
                {
                        EventFormatTypes.OFFLINE,
                        new HashMap<String, String>() {{
                            put("regionCode", "78");
                        }}
                },
                {
                        EventFormatTypes.HYBRID,
                        new HashMap<String, String>() {{
                            put("link", "\"https://ya.ru\"");
                            put("regionCode", "78");
                        }}
                }
        };
    }

    //Список параметров для проверки метода создания события с максимальным набором данных
    @DataProvider(name = "maxUniqueFields")
    public Object[][] createWithMaximumFieldsParams() {
        return new Object[][]{
                {
                        EventFormatTypes.ONLINE,
                        new HashMap<String, String>() {{
                            put("link", "\"https://ya.ru\"");
                        }}
                },
                {
                        EventFormatTypes.OFFLINE,
                        new HashMap<String, String>() {{
                            put("districtCode", "02_003");
                            put("regionCode", "02");
                            put("cityName", Utils.randomRussianWords(1));
                            put("address", Utils.randomRussianWords(5));
                        }}
                },
                {
                        EventFormatTypes.HYBRID,
                        new HashMap<String, String>() {{
                            put("link", "\"https://ya.ru\"");
                            put("districtCode", "02_003");
                            put("regionCode", "02");
                            put("cityName", Utils.randomRussianWords(1));
                            put("address", Utils.randomRussianWords(5));
                        }}
                }
        };
    }

    @Test(testName = "CPO-T368 Создание нового события с минимальным набором данных",
            dataProvider = "форматы событий и уникальные поля"
    )
    public void создание_нового_события_с_минимальным_набором_данных(
            EventFormatTypes format, HashMap<String, String> uniqueFields) {
        JSONObject rawRequestBody = new JSONObject()
                .put("format", format.toString())
                .put("type", EventTypes.PROJECT)
                .put("header", "CPO-T368: " + format + " " + Utils.randomRussianWords(3))
                .put("detailInfo", EventsUtils.generateEmptyDetailInfo())
                .put("timeZone", EventTimeZones.MSK.toString())
                .put("orderForm", EventsUtils.generateEmptyOrderForm())
                .put("cover", EventsUtils.generateCover(EventCoverImageTypes.GREEN))
                .put("dateTimeStart",
                        OffsetDateTime.ofInstant(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()),
                                ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES));

        JSONObject location = new JSONObject();
        for (Map.Entry<String, String> entry : uniqueFields.entrySet()
        ) {
            location.put(entry.getKey(), entry.getValue());
        }

        rawRequestBody.put("location", location);

        Response response = EventAdminRequests.createEvent(rawRequestBody.toString());

        String eventId = response.jsonPath().getString("id");

        eventIdsToDelete.add(eventId);

        //Публикуем событие
        EventAdminRequests.changeEventStatus(eventId, EventStatuses.PUBLISHED);

        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("status", EventStatuses.PUBLISHED)).toString();
        //toDo
        Response eventList = EventAdminRequests.getEventList("", listFilter);

        Assert.assertEquals(eventList.jsonPath().getString("[0].id"), eventId,
                "Не нашли значение первым в списке: "+eventId);
    }


    @Test(testName = "CPO-T370 Создание черновика события с минимальным набором данных",
            dataProvider = "форматы событий и уникальные поля"
    )
    public void создание_черновика_события_с_минимальным_набором_данных(
            EventFormatTypes format, HashMap<String, String> uniqueFields) {
        JSONObject rawRequestBody = new JSONObject()
                .put("format", format.toString())
                .put("type", EventTypes.PROJECT)
                .put("header", "CPO-T370: " + format + " " + Utils.randomRussianWords(3))
                .put("detailInfo", EventsUtils.generateEmptyDetailInfo())
                .put("orderForm", EventsUtils.generateEmptyOrderForm());

        Response response = EventAdminRequests.createEvent(rawRequestBody.toString());

        String eventId = response.jsonPath().getString("id");

        eventIdsToDelete.add(eventId);

        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("status", EventStatuses.DRAFT)).toString();
        //toDo
        Response eventList = EventAdminRequests.getEventList("", listFilter);

        Assert.assertEquals(eventList.jsonPath().getString("[0].id"), eventId,
                "Не нашли значение первым в списке: "+eventId);
    }

    //также покрывает сценарий T-379
    @Test(testName = "CPO-T374 Создание события с максимальным набором данных",
            dataProvider = "maxUniqueFields"
    )
    public void создание_нового_черновика_события_с_максимальным_набором_данных(
            EventFormatTypes format, HashMap<String, String> uniqueFields) {
        JSONObject rawRequestBody = new JSONObject()
                .put("type", EventTypes.EVENT)
                .put("header", "CPO-T374: " + format + " " + Utils.randomRussianWords(3))
                .put("shortDescription", Utils.randomRussianWords(10))
                .put("format", format.toString())
                .put("dateTimeStart",
                        OffsetDateTime.ofInstant(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()),
                                ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .put("dateTimeEnd",
                        OffsetDateTime.ofInstant(LocalDateTime.now().plusDays(2)
                                        .toInstant(OffsetDateTime.now().getOffset()),
                                ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .put("timeZone", EventTimeZones.MSK)
                .put("detailInfo", EventsUtils.generateFullDetailInfo())
                .put("cover", EventsUtils.generateCover(EventCoverImageTypes.blue))
                .put("orderForm", EventsUtils.generateFullOrderForm())
                .put("recivers", EventsUtils.generateReceiverEmployee(
                        new JSONArray()
                                .put(Users.getUser("02022035663").userId.toString()),
                        true,
                        true
                ));

        JSONObject location = new JSONObject();
        for (Map.Entry<String, String> entry : uniqueFields.entrySet()
        ) {
                location.put(entry.getKey(), entry.getValue());
        }

        rawRequestBody.put("location", location);

        Response response = EventAdminRequests.createEvent(rawRequestBody.toString());

        String eventId = response.jsonPath().getString("id");

        eventIdsToDelete.add(eventId);

        //Публикуем событие
        EventAdminRequests.changeEventStatus(eventId, EventStatuses.PUBLISHED);

        String listFilter = new JSONObject()
                .put("filters", new JSONObject()
                        .put("status", EventStatuses.PUBLISHED)).toString();
        //toDo
        Response eventList = EventAdminRequests.getEventList("", listFilter);

        Assert.assertEquals(eventList.jsonPath().getString("[0].id"), eventId,
                "Не нашли значение первым в списке: "+eventId);
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
