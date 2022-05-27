package ru.digital.services.sp.API;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.annotation.Obsolete;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;
import static ru.digital.services.sp.API.BaseApi.*;

@Obsolete
//Необходимо разнести свои методы по папкам и использовать базовые методы из BaseApi
public class API {
    public static final String abservicePath = "api/abservice/";
    public static final String vttPath = "api/vtt/v1/";

    //----------------------------------------------------Main functions----------------------------------------------------
    public static String getSelfGuid() {
        Response response =
                given().header("Authorization", "Bearer " + accessToken)
                        .baseUri(url)
                        .basePath("api/")
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON)
                        .when()
                        .get("personal-data-service")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract().response();
        return response.path("guid");
    }

    public static String getSelfSNILS() {
        Response response =
                given().header("Authorization", "Bearer " + accessToken)
                        .baseUri(url)
                        .basePath("api/")
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON)
                        .when()
                        .get("personal-data-service")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract().response();
        return response.path("snils");
    }

    public static Object vttIsAvailable() {
        Response response =
                given().header("Authorization", "Bearer " + accessToken)
                        .baseUri(url)
                        .basePath(vttPath)
                        .contentType("application/json")
                        .queryParam("info", "true")
                        .body(new JSONObject().put("year", 2021)
                                .toString())
                        .when()
                        .post()
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract().response();
        return response.path("passengersInfo.emploeyeeAlreadyDesigned");
    }

    public static Response getAppList() {
        return
                given().header("Authorization", "Bearer " + accessToken)
                        .baseUri(url)
                        .basePath(vttPath)
                        .contentType("application/json")
                        .queryParam("start", 0)
                        .queryParam("pageSize", 100)
                        .when()
                        .get()
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response();
    }

    public static void deleteVtt(String guid) {
        getBearerRequestWithCreds(null)
                .basePath(vttPath + guid)
                .when()
                .delete()
                .then()
                .assertThat()
                .statusCode(200);
    }
}