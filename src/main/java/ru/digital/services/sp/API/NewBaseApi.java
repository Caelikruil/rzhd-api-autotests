package ru.digital.services.sp.API;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.annotation.Obsolete;
import ru.digital.services.sp.Props;
import ru.digital.services.sp.Users.User;

import static io.restassured.RestAssured.given;

public abstract class NewBaseApi {

    //---------------------------------------------------Const--------------------------------------------------------

    //Константы для авторизационных заголовков
    private final String AUTH_HEADER = "Authorization";
    private final String BASIC_TOKEN = "Basic";
    private final String BEARER_TOKEN = "Bearer";

    //Авторизационный токен пользователя
    private final String accessToken = Auth.getToken();

    //Базовый урл окружения
    private final String url = Props.get("backend.url");
    private String basePath = "";
    private String baseIntPath = "";


    //---------------------------------------------------Methods--------------------------------------------------------
    //Запрос к сервису по пользовательской авторизации конкретного юзера
    public RequestSpecification getBearerRequestWithCreds(User user) {
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

    //Запрос к сервису по сервисной авторизации
    @Obsolete()
    public RequestSpecification getBasicRequest() {

        Header authHeader = new Header(AUTH_HEADER, BASIC_TOKEN + " " + Auth.commonToken);

        return given().
                header(authHeader)
                .baseUri(url)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .basePath(getBaseIntPath());
    }

    protected String getBasePath() {
        return this.basePath;
    }
    protected String getBaseIntPath() {return this.baseIntPath;}
}
