package ru.digital.services.sp.API;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import ru.digital.services.sp.Props;

import java.nio.charset.Charset;

import static io.restassured.RestAssured.given;

public class EmployeeApi {

    //Константы для авторизационных заголовков
    private static final String AUTH_HEADER = "Authorization";
    private static final String BASIC_TOKEN = "Basic";
    //Базовый урл окружения
    public static final String url = Props.get("fileserver.url");
    //Базовый путь сервиса reception
    private static final String employeePath = "employee/";
    public static String commonToken = Props.getOrEnv("fileserver.token.basic");

    //чтение файла
    //filePath - адрес формата H24000/020-220-35663.xml
    //имя файла можно получить методом User.getSnilsXmlFileName()
    public static Response getUserPackage(String filePath) {
        return given().
                header(AUTH_HEADER, BASIC_TOKEN + " " + commonToken)
                .baseUri(url)
                .basePath(employeePath + filePath)
                .when()
                .get()
                .then()
                //.log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //редактирование файла
    public static void changeUserPackage(String filePath, String content) {
        given().
                header(AUTH_HEADER, BASIC_TOKEN + " " + commonToken)
                .baseUri(url)
                .contentType("text/xml; charset=UTF-8")
                .basePath(employeePath + filePath)
                .body(content)
                .when()
                .put()
                .then()
                //.log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

}
