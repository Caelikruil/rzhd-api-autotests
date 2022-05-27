package ru.digital.services.sp.API.ReceptionRequests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import ru.digital.services.sp.API.BaseApi;
import ru.digital.services.sp.Dictionaries.WebPlatforms;
import ru.digital.services.sp.Users.User;

import java.util.Map;

import static ru.digital.services.sp.API.BaseApi.getBasicRequest;
import static ru.digital.services.sp.API.BaseApi.getBearerRequestWithCreds;

public class ReceptionRequests {

    //Базовый путь сервиса reception-int
    private static final String receptionIntPath = "api/reception-int/v1/";
    //Базовый путь сервиса reception
    private static final String receptionPath = "api/reception/v1/";

    //проверка доступности функционала «Вопрос руководителю»
    //platform - платформа доступа
    public static Response checkReceptionAvailable(User user, WebPlatforms platform) {
        return getBearerRequestWithCreds(user)
                .basePath(receptionPath + "isAccessQuestion")
                .queryParam("platform", platform)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Получить руководителя
    public static Response getManager(User user, Map<String, String> queryParams) {
        return getBearerRequestWithCreds(user)
                .basePath(receptionPath + "manager")
                .queryParams(queryParams)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //создание обращения
    public static Response createQuestion(User user, String requestBody) {
        return BaseApi.getBearerRequestWithCreds(user)
                .basePath(receptionPath + "question")
                .body(requestBody)
                .when()
                .log().body()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Получить список заявок
    public static Response getOrderList(User user, Map<String, String> queryParams) {
        return getBearerRequestWithCreds(user)
                .basePath(receptionPath + "orderList")
                .queryParams(queryParams)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Получить заявку
    public static Response getOrder(User user, String orderId) {
        return getBearerRequestWithCreds(user)
                .basePath(receptionPath + "order/" + orderId)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Получить вопрос
    public static Response getQuestion(User user, String questionId) {
        return getBearerRequestWithCreds(user)
                .basePath(receptionPath + "question/" + questionId)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //отправить ответ на вопрос
    public static void sendAnswer(String questionId, String requestBody) {
        getBasicRequest()
                .basePath(receptionIntPath + "reciveAnswer/" + questionId)
                .body(requestBody)
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    public static Response isCanEscalate(User user) {
        return getBearerRequestWithCreds(user)
                .basePath(receptionPath + "isCanEscalate")
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    public static Response getFile(User user, String fileId) {
        return getBearerRequestWithCreds(user)
                .basePath(receptionPath + "file/" + fileId)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    public static Response getRateCriteria(User user, Map<String, String> queryParams) {
        return getBearerRequestWithCreds(user)
                .basePath(receptionPath + "comment")
                .queryParams(queryParams)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    public static void rateAnswer(User user, String orderId, String requestBody) {
        getBearerRequestWithCreds(user)
                .basePath(receptionPath + "rate/" + orderId)
                .body(requestBody)
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }
}
