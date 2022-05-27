package ru.digital.services.sp.API.EducationRequests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import ru.digital.services.sp.Users.User;

import static ru.digital.services.sp.API.BaseApi.*;

//Класс-хранилище запросов в сервисы Education и EducationIntegration
public class EducationRequests {

    //Базовый путь сервиса education-int
    private static final String educationIntPath = "api/education-int/v1/";
    //Базовый путь сервиса education
    private static final String educationPath = "api/education/v1/";

    //Метод добавления и изменения курса пользователю
    //requestBody - массив данных о курсе для пользователей в строковом представлении
    public static Response sendCourseFromSDOtoSP(String requestBody, int expectedStatus) {
        return getBasicRequest()
                .basePath(educationIntPath + "course")
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(expectedStatus)
                .extract().response();
    }

    //Метод получения списка назначенных пользователю курсов
    //возвращает массив list с объектами
    public static Response getUserAssignedCourseList(User user) {
        return getBearerRequestWithCreds(user)
                .basePath(educationPath + "courseList")
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
    }

    //Метод изменения статуса прохождения курса пользователем
    //requestBody - массив данных о новых статусах курсов пользователей
    public static Response setCourseStatus(String requestBody, int expectedStatusCode) {
        return getBasicRequest()
                .basePath(educationIntPath + "status")
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(expectedStatusCode)
                .extract().response();
    }
}
