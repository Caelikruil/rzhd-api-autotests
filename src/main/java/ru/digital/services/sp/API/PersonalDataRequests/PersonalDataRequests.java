package ru.digital.services.sp.API.PersonalDataRequests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import ru.digital.services.sp.Users.User;

import static ru.digital.services.sp.API.BaseApi.getBearerRequestWithCreds;

public class PersonalDataRequests {
    //Базовый путь сервиса personal-data
    private static final String persdataPath = "api/personal-data-service/";

    //Получение перс даты пользователя
    public static Response getPersData(User user) {
        return getBearerRequestWithCreds(user)
                .basePath(persdataPath)
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }
}
