package ru.digital.services.sp.API.HousingPolicyRequests;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.digital.services.sp.API.BaseApi;
import ru.digital.services.sp.API.RequestPayload;

import static ru.digital.services.sp.API.BaseApi.*;

//Класс-хранилище запросов в сервисы Льготной ипотеки - housingpolicy
public class HousingPolicyIntRequests {

    //Базовый путь сервиса housing-policy-int
    private static final String housingIntPath = "api/housingpolicy-int/v1/";


    //Метод добавления нормы квадратных метров по региону
    //requestBody - массив норм в строковом представлении
    public static void addSquareMetersNorm(String requestBody) {
        getBasicRequest()
                .basePath(housingIntPath + "dictNormsSquareMetersLivingSpace")
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод добавления стоимости квадратных метров по региону
    //requestBody - массив норм в строковом представлении
    public static void addSquareMetersCost(String requestBody) {
        getBasicRequest()
                .basePath(housingIntPath + "dictCostMeter")
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод добавления дирекции
    //requestBody - массив дирекций в строковом представлении
    public static void addDirections(String requestBody)
    {
        getBasicRequest()
                .basePath(housingIntPath + "dictDirections")
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод добавления связи дирекции и организации
    //direction - код дирекции
    //requestBody - массив объектов с OrgCode в строковом представлении
    public static void addDirectionOrgRelations(String direction, String requestBody)
    {
        getBasicRequest()
                .basePath(housingIntPath + "dictDirectionOrg/"+direction)
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод добавления профессии
    //direction - код дирекции
    //requestBody - массив объектов с профессиями в строковом представлении
    public static void addApprovedProfessions(String direction, String requestBody)
    {
        getBasicRequest()
                .basePath(housingIntPath + "dictApprovedProfessions/"+direction)
                .body(requestBody)
                .log().body()
                .when()
                .post()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод удаления нормы квадратных метров по региону
    //normId - идентификатор нормы
    public static void deleteSquareMetersNorm(String normId) {
        String requestBody = new JSONArray().put(new JSONObject()
                .put("id", normId)).toString();

        getBasicRequest()
                .basePath(housingIntPath + "dictNormsSquareMetersLivingSpace")
                .body(requestBody)
                .log().body()
                .when()
                .delete()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод удаления нормы стоимости квадратных метров по региону
    //normId - идентификатор нормы
    public static void deleteSquareMetersCost(String normId) {
        String requestBody = new JSONArray().put(new JSONObject()
                .put("id", normId)).toString();

        getBasicRequest()
                .basePath(housingIntPath + "dictCostMeter")
                .body(requestBody)
                .log().body()
                .when()
                .delete()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод удаления дирекции и связанных с ней элементов (связь с оргструктурой, профессий)
    //directionCode - идентификатор дирекции
    public static void deleteDirection(String directionCode) {
        String requestBody = new JSONArray().put(new JSONObject()
                .put("code", directionCode)).toString();

        getBasicRequest()
                .basePath(housingIntPath + "dictDirections")
                .body(requestBody)
                .log().body()
                .when()
                .delete()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод удаления связи дирекции и оргструктуры
    //directionCode - идентификатор дирекции
    //orgCode - код оргструктуры
    public static void deleteDirectionAndOrgRelation(String directionCode, String orgCode) {
        String requestBody = new JSONArray().put(new JSONObject()
                .put("code", orgCode)).toString();

        getBasicRequest()
                .basePath(housingIntPath + "dictDirectionOrg/"+directionCode)
                .body(requestBody)
                .log().body()
                .when()
                .delete()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    //Метод удаления связи дирекции и оргструктуры
    //directionCode - идентификатор дирекции
    //requestBody - массив профессий к удалению в массиве приведенному к строке
    public static void deleteApprovedProfession(String directionCode, String requestBody) {
        getBasicRequest()
                .basePath(housingIntPath + "dictApprovedProfessions/"+directionCode)
                .body(requestBody)
                .log().body()
                .when()
                .delete()
                .then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }


    public static void changeApplicationRequestStatus(RequestPayload payload)
    {
        BaseApi.postIntRequest(
                housingIntPath + "appRegistrationChangeStatus",
                payload
        );
    }

}
