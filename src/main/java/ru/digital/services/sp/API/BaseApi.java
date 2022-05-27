package ru.digital.services.sp.API;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.annotation.Obsolete;
import org.apache.http.auth.AUTH;
import ru.digital.services.sp.Props;
import ru.digital.services.sp.Users.User;

import static io.restassured.RestAssured.given;

public class BaseApi {

    //---------------------------------------------------Const--------------------------------------------------------

    //Константы для авторизационных заголовков
    private static final String AUTH_HEADER = "Authorization";
    private static final String BASIC_TOKEN = "Basic";
    private static final String BEARER_TOKEN = "Bearer";

    //Авторизационный токен пользователя
    public static final String accessToken = Auth.getToken();

    //Базовый урл окружения
    public static final String url = Props.get("backend.url");
    private static String basePath = "";


    //---------------------------------------------------Methods--------------------------------------------------------
    //Запрос к сервису по пользовательской авторизации конкретного юзера
    @Obsolete
    public static RequestSpecification getBearerRequestWithCreds(User user) {
        Header authHeader = new Header(AUTH_HEADER, user == null
                ? ""
                : BEARER_TOKEN + " " + Auth.getToken(user.snils, user.password));
        return given()
                .header(authHeader)
                .baseUri(url)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .basePath(getBasePath());
    }

    public static Response getUserRequest(String path, RequestPayload payload)
    {
        return getBearerRequestWithCreds(payload.user)
                .basePath(path)
                .queryParams(payload.queryParams)
                .when()
                .get()
                .then()
                .assertThat()
                .statusCode(payload.expectedCode)
                .extract().response();
    }
    public static Response postUserRequest(String path, RequestPayload payload)
    {
        return getBearerRequestWithCreds(payload.user)
                .basePath(path)
                .queryParams(payload.queryParams)
                .body(payload.body)
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(payload.expectedCode)
                .extract().response();
    }

    public static Response putUserRequest(String path, RequestPayload payload)
    {
        return getBearerRequestWithCreds(payload.user)
                .basePath(path)
                .queryParams(payload.queryParams)
                .body(payload.body)
                .when()
                .put()
                .then()
                .log().body()
                .assertThat()
                .statusCode(payload.expectedCode)
                .extract().response();
    }

    public static Response deleteUserRequest(String path, RequestPayload payload)
    {
        return getBearerRequestWithCreds(payload.user)
                .basePath(path)
                .queryParams(payload.queryParams)
                .body(payload.body)
                .when()
                .delete()
                .then()
                .log().body()
                .assertThat()
                .statusCode(payload.expectedCode)
                .extract().response();
    }

    //Запрос к сервису по сервисной авторизации
    @Obsolete
    public static RequestSpecification getBasicRequest(boolean withAuth) {
        Header authHeader = new Header(AUTH_HEADER,
                withAuth
                        ? BASIC_TOKEN + " " + Auth.commonToken
                        : "");

        return given().
                header(authHeader)
                .baseUri(url)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    //Запрос к сервису по сервисной авторизации
    @Obsolete()
    public static RequestSpecification getBasicRequest() {
        return given().
                header(AUTH_HEADER, BASIC_TOKEN + " " + Auth.commonToken)
                .baseUri(url)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    public static Response postIntRequest(String path, RequestPayload payload)
    {
        return getBasicRequest(payload.withAuth)
                .basePath(path)
                .queryParams(payload.queryParams)
                .body(payload.body)
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(payload.expectedCode)
                .extract().response();
    }

    private static String getBasePath()
    {
        return basePath;
    }
}
