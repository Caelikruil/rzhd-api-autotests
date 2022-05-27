package ru.digital.services.sp;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.API;
import ru.digital.services.sp.API.BaseApi;
import ru.digital.services.sp.Utils.Utils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class AbserviceNegativeTests {
    private static final String url = BaseApi.url;
    private static final String path = API.abservicePath;
    private static final String accessToken ="Bearer " + BaseApi.accessToken;

    @Test()
    public void
    получение_пользователя() {
        String wrongData = Utils.randomAlphaNumericEN(5);
        given().header("Authorization", "Bearer " + accessToken)
            .baseUri(url)
            .basePath(path)
            .contentType("application/json")
            .queryParam("start", wrongData)
            .queryParam("pageSize", wrongData)
            .queryParam("sortOrder", wrongData)
            .queryParam("sortField", wrongData)
            .queryParam("query", wrongData)
            .queryParam("plansText", wrongData)
            .queryParam("favorites", wrongData)
            .queryParam("searchMode", wrongData)
            .when()
            .post("employee")
            .then()
            .assertThat()
            .statusCode(400)
            .body("apierror.status", equalTo("BAD_REQUEST"));
    }
}
