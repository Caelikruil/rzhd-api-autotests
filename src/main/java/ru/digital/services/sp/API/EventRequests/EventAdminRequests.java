package ru.digital.services.sp.API.EventRequests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import ru.digital.services.sp.Dictionaries.AdminRoles;
import ru.digital.services.sp.Dictionaries.Events.Admin.CancelCauses;
import ru.digital.services.sp.Dictionaries.Events.Admin.EventStatuses;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import static ru.digital.services.sp.API.BaseApi.getBearerRequestWithCreds;

public class EventAdminRequests {

    //Базовый путь сервиса education
    private static final String eventPath = "api/event/v1/";

    //Юзер с правами администратора событий
    private static final User admin = Users.getUserByRole(AdminRoles.adminEvent);

    //Метод получения списка событий
    public static Response getEventList(String queryParams, String requestBody) {
        return getBearerRequestWithCreds(admin)
                .basePath(eventPath + "eventList" + queryParams)
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Метод создания и редактирования события
    public static Response createEvent(String requestBody) {
        return getBearerRequestWithCreds(admin)
                .basePath(eventPath + "event")
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Метод получения события
    public static Response getEvent(String eventId) {
        return getBearerRequestWithCreds(admin)
                .basePath(eventPath + "event/" + eventId)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Метод удаления события
    public static void deleteEvent(String eventId) {
        getBearerRequestWithCreds(admin)
                .basePath(eventPath + "event/" + eventId)
                .when()
                .delete()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод получения количества участников
    public static Response getReceiversCount(String requestBody)
    {
        return getBearerRequestWithCreds(admin)
                .basePath(eventPath + "reciversCount")
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Метод получения приложенного файла
    public static Response GetReport(String eventId)
    {
        return getBearerRequestWithCreds(admin)
                .basePath(eventPath + "getReport/" + eventId)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Метод смены статуса События
    public static Response changeEventStatus(String eventId, EventStatuses status)
    {
        String requestBody = new JSONObject()
                .put("status", status.toString()).toString();

        return getBearerRequestWithCreds(admin)
                .basePath(eventPath + "eventChangeStatus/" + eventId)
                .body(requestBody)
                .log().body()
                .when()
                .put()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Метод снятия События с публикации
    public static Response cancelEvent(String eventId, CancelCauses cause, String text)
    {
        String requestBody = new JSONObject()
                .put("status", EventStatuses.DRAFT.toString())
                .put("causePublishCancel", new JSONObject()
                        .put("code", cause.toString())
                        .put("text", text))
                .toString();

        return getBearerRequestWithCreds(admin)
                .basePath(eventPath + "eventChangeStatus/" + eventId)
                .body(requestBody)
                .log().body()
                .when()
                .put()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }
}
