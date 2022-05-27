package ru.digital.services.sp.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.digital.services.sp.Dictionaries.Events.*;
import ru.digital.services.sp.Dictionaries.Events.Admin.EventCoverImageTypes;
import ru.digital.services.sp.Dictionaries.Events.Admin.EventSendTypes;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.SQL.PostgreSQLConnection;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class EventsUtils {

    public static final JSONArray emptyArray = new JSONArray();
    public static final JSONObject fileObject = new JSONObject()
            //картинка с точками разных цветов 5 на 5 пикселей в формате base64
            .put("name", "dots5x5.jpg")
            .put("data", Utils.getBase64ImageDate());

    public static JSONObject generateEmptyOrderForm() {
        return new JSONObject()
                .put("emailReciveOrder", "")
                .put("fields", emptyArray);
    }

    public static JSONObject generateFullOrderForm() {

        JSONArray fieldArray = new JSONArray();

        for (EventOrderFieldTypes type : EventOrderFieldTypes.values()) {
            fieldArray.put(new JSONObject()
                    .put("name", type.toString() + " тип поле")
                    .put("type", type));
        }

        return new JSONObject()
                //тут нужна общая почта для автотестов
                .put("emailReciveOrder", "")
                .put("fields", fieldArray);
    }

    public static JSONArray generateEmptyDetailInfo() {
        return new JSONArray()
                .put(new JSONObject()
                        .put("header", JSONObject.NULL)
                        .put("description", JSONObject.NULL)
                        .put("files", emptyArray)
                        .put("links", new JSONArray()
                                .put(new JSONObject()
                                        .put("portal", new JSONObject()
                                                .put("url", JSONObject.NULL)
                                                .put("text", JSONObject.NULL)
                                        )
                                )
                        )
                );
    }

    public static JSONArray generateFullDetailInfo() {
        return new JSONArray()
                .put(new JSONObject()
                        .put("header", Utils.randomRussianWords(14))
                        .put("description", Utils.randomRussianWords(20))
                        .put("files", new JSONArray()
                                .put(fileObject))
                        .put("links", new JSONArray()
                                //внешняя ссылка
                                .put(new JSONObject()
                                        .put("external", new JSONObject()
                                                .put("url", "https://ya.ru/")
                                                .put("text", "Поиск яндекс")
                                        )
                                )
                                //внутренняя ссылка
                                .put(new JSONObject()
                                        .put("portal", new JSONObject()
                                                .put("url", "development")
                                                .put("text", "Мое развитие")
                                        )
                                )
                        )
                );
    }

    public static JSONObject generateCover(EventCoverImageTypes gradientId) {
        return gradientId != EventCoverImageTypes.FILE ? new JSONObject().put("gradientId", gradientId)
                : new JSONObject().put("file", fileObject);
    }

    public static JSONObject generateReceiverEmployee(JSONArray employees,
                                                      boolean isShowAttendeesInCard, boolean isSendNotification) {
        return new JSONObject()
                .put("sendType", EventSendTypes.INDIVIDUAL)
                .put("employeeIds", employees)
                .put("isShowAttendeesInCard", isShowAttendeesInCard)
                .put("isSendNotification", isSendNotification);
    }

    //максимально заполненное хардкодное тело события для его использования везде
    public static JSONObject generateCreateEventRequestBody() {
        return new JSONObject()
                .put("type", EventTypes.EVENT)
                .put("header", "CPO-T000: Онлайн" + Utils.randomRussianWords(3))
                .put("shortDescription", Utils.randomRussianWords(10))
                .put("format", EventFormatTypes.ONLINE)
                .put("dateTimeStart",
                        OffsetDateTime.ofInstant(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()),
                                ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .put("dateTimeEnd",
                        OffsetDateTime.ofInstant(LocalDateTime.now().plusDays(2)
                                        .toInstant(OffsetDateTime.now().getOffset()),
                                ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .put("timeZone", EventTimeZones.MSK)
                .put("detailInfo", new JSONArray()
                        .put(new JSONObject()
                                .put("header", Utils.randomRussianWords(14))
                                .put("description", Utils.randomRussianWords(20))
                                .put("files", new JSONArray()
                                        .put(fileObject))
                                .put("links", new JSONArray()
                                        //внешняя ссылка
                                        .put(new JSONObject()
                                                .put("external", new JSONObject()
                                                        .put("url", "https://ya.ru/")
                                                        .put("text", "Поиск яндекс")
                                                )
                                        )
                                        //внутренняя ссылка
                                        .put(new JSONObject()
                                                .put("portal", new JSONObject()
                                                        .put("url", "development")
                                                        .put("text", "Мое развитие")
                                                )
                                        )
                                )
                        ))
                .put("cover", new JSONObject().put("file", fileObject))
                .put("orderForm", new JSONObject()
                        //тут нужна общая почта для автотестов
                        .put("emailReciveOrder", "")
                        .put("fields", new JSONArray().put(new JSONObject()
                                .put("name", EventOrderFieldTypes.TEXT + " тип поле")
                                .put("type", EventOrderFieldTypes.TEXT))))
                .put("location", new JSONObject()
                        .put("link", "\"https://ya.ru\"")
                        .put("regionCode", "02"))
                .put("recivers", EventsUtils.generateReceiverEmployee(
                        new JSONArray()
                                .put("caca592d-8c2e-4842-a41b-37ba64a22340"),
                        true,
                        true
                ));
    }

    public static void insertRandomEventParticipant(String eventId, int userCount) {
        String sqlRequest = "INSERT INTO es_event_participant_order " +
                "SELECT gen_random_uuid(), '" + eventId + "', guid, false, 'SENDED', NOW(), NOW() " +
                "FROM event_employee " +
                "WHERE guid NOT IN (SELECT employee_id FROM es_event_participant_order " +
                "WHERE event_id = '"+eventId+"')" +
                " limit " + userCount + ";";
        PostgreSQLConnection.changeDataBySQL(db.event, sqlRequest);
    }
}
