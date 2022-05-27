package ru.digital.services.sp.API.BenefitRequests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import ru.digital.services.sp.Users.User;

import static ru.digital.services.sp.API.BaseApi.getBasicRequest;
import static ru.digital.services.sp.API.BaseApi.getBearerRequestWithCreds;

public class BenefitRequests {

    //Базовый путь сервиса education-int
    private static final String benefitIntPath = "api/benefit-int/v1";
    //Базовый путь сервиса education
    private static final String benefitPath = "api/benefit/v1";

    //Метод добавления льгот пользователю
    public static void sendBenefits(String requestBody) {
        getBasicRequest()
                .basePath(benefitIntPath + "/")
                .body(requestBody)
                .log().all()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод запроса данных по льготам
    public static void sendRequest(User user) {
        getBearerRequestWithCreds(user)
                .basePath(benefitPath + "/sendRequest")
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод получения списка льгот
    public static Response getBenefits(User user) {
        return getBearerRequestWithCreds(user)
                .basePath(benefitPath + "/benefits")
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    //Метод получения статуса по запросу данных о льготах
    public static Response getRequestsStatus(User user) {
        return getBearerRequestWithCreds(user)
                .basePath(benefitPath + "/status")
                .when()
                .get()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
    }
}
