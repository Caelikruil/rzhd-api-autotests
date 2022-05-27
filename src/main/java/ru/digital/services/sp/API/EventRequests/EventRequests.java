package ru.digital.services.sp.API.EventRequests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import ru.digital.services.sp.Users.User;

import static ru.digital.services.sp.API.BaseApi.*;

//Класс хранилище запросов к сервису событий
public class EventRequests {

    //Базовый путь сервиса education
    private static final String eventPath = "api/event/v1/";

    //Метод получения текущего выбранного пользователем региона просмотра Событий
    public static Response getUserRegion(User user) {
        return
                getBearerRequestWithCreds(user)
                        .basePath(eventPath + "region")
                        .when()
                        .get()
                        .then()
                        .log().body()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .response();
    }

    //Метод выбора пользователем региона просмотра Событий
    public static void setUserRegion(User user, String regionId) {
        getBearerRequestWithCreds(user)
                .basePath(eventPath + "region/" + regionId)
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод создания заявки на участие в событии
    public static void createOrder(User user, String eventId, String requestBody) {
        getBearerRequestWithCreds(user)
                .basePath(eventPath + "order/" + eventId)
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод получения заявки на участие в событии
    public static Response getOrder(User user, String eventId) {
        return
                getBearerRequestWithCreds(user)
                        .basePath(eventPath + "order/" + eventId)
                        .when()
                        .get()
                        .then()
                        .log().body()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .response();
    }

    //Метод смены статуса заявки
    public static void changeOrderStatus(User user, String eventId, String requestBody) {
        getBearerRequestWithCreds(user)
                .basePath(eventPath + "orderChangeStatus/" + eventId)
                .body(requestBody)
                .log().body()
                .when()
                .put()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Добавление события в избранные
    public static void makeEventFavorite(User user, String eventId) {
        getBearerRequestWithCreds(user)
                .basePath(eventPath + "isFavorite/" + eventId)
                .when()
                .log().uri()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Удаление события из избранных
    public static void removeEventFromFavorite(User user, String eventId) {
        getBearerRequestWithCreds(user)
                .basePath(eventPath + "isFavorite/" + eventId)
                .when()
                .log().uri()
                .delete()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод получения списка событий
    public static Response getUserEventList(User user, String queryParams, String requestBody)
    {
        return getBearerRequestWithCreds(user)
                .basePath(eventPath + "eventUserList" + queryParams)
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

    //Метод получения количества участников события
    public static Response getEventParticipantCount(User user, String eventId, String requestBody)
    {
        return getBearerRequestWithCreds(user)
                .basePath(eventPath + "participantCount/" + eventId)
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

    //Метод получения списка участников события
    public static Response getEventParticipantList(User user, String queryParams, String requestBody)
    {
        return getBearerRequestWithCreds(user)
                .basePath(eventPath + "participantList/" + queryParams)
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
    public static Response GetAttachedFile(User user, String fileId)
    {
        return getBearerRequestWithCreds(user)
                .basePath(eventPath + "getFile/" + fileId)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }
}
