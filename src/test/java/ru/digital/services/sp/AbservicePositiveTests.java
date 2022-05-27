package ru.digital.services.sp;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.API;
import ru.digital.services.sp.API.BaseApi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class AbservicePositiveTests {
    private static final String url = BaseApi.url;
    private static final String path = API.abservicePath;
    private static final String accessToken ="Bearer " + BaseApi.accessToken;

@DataProvider(name = "StartPageSize")
public Object[][] searchName() {
    return new Object[][] {
            {0, 20},
            {1, 40},
    };
}
//---------------------------------------------------GET----------------------------------------------------------------
    @Test()
    public void
    получение_версии() {
        String expResult = "git.commit.id";
        Response response =
            given().header("Authorization", "Bearer " + accessToken)
                .baseUri(url)
                .basePath(path)
                .contentType("application/json")
                .when()
                .get("version/")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response();
        Assert.assertTrue(response.asString().contains(expResult));
}

    @Test()
    public void
    получение_собственных_персональных_данных() {
        String guid = API.getSelfGuid();
        Response response =
                given().header("Authorization", "Bearer " + accessToken)
                        .baseUri(url)
                        .basePath("api/")
                        .accept("application/json")
                        .contentType("application/json")
                        .when()
                        .get("personal-data-service")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract().response();
        Assert.assertEquals(response.path("guid"), guid);
    }

//---------------------------------------------------POST---------------------------------------------------------------
    @Test()
    public void
    получение_отпуска() {
        given().header("Authorization", "Bearer " + accessToken)
                .baseUri(url)
                .basePath(path)
                .contentType("application/json")
                .queryParam("start", 0)
                .queryParam("pageSize", 20)
                .queryParam("query", "Федорова")
                .queryParam("excludeId", "irure pariatur sint,in cupidatat")
                .when()
                .post("employeeVacation")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response();
    }
    @Test()
    public void
    получение_пользователя() {
        given().header("Authorization", "Bearer " + accessToken)
            .baseUri(url)
            .basePath(path)
            .contentType("application/json")
            .queryParam("start", 0)
            .queryParam("pageSize", 20)
            .queryParam("sortOrder", "ASC")
            .queryParam("sortField", "officia cupidatat veniam")
            .queryParam("query", "officia cupidatat veniam")
            .queryParam("plansText", "officia cupidatat veniam")
            .queryParam("favorites", false)
            .queryParam("searchMode", "PARTIAL_MATCH")
            .when()
            .post("employee")
            .then()
            .assertThat()
            .statusCode(200)
            .body("meta.total", equalTo(0))
            .body("meta.paging.start", equalTo(0))
            .body("meta.paging.pageSize", equalTo(20));
    }

    @Test(dataProvider = "StartPageSize")
    public void
    поиск_контакта_или_подразделения(int start, int pageSize) {
        given().header("Authorization", "Bearer " + accessToken)
            .baseUri(url)
            .basePath(path)
            .contentType("application/json")
            .queryParam("start", start)
            .queryParam("pageSize", pageSize)
            .queryParam("query", "officia cupidatat veniam")
            .queryParam("favorites", false)
            .when()
            .post("org")
            .then()
            .assertThat()
            .statusCode(200)
            .body("meta.total", equalTo(0))
            .body("meta.paging.start", equalTo(start))
            .body("meta.paging.pageSize", equalTo(pageSize));
    }
//---------------------------------------------------PUT----------------------------------------------------------------

//---------------------------------------------------DELETE-------------------------------------------------------------
}
