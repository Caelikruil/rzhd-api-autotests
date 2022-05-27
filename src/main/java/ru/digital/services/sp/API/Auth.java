package ru.digital.services.sp.API;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import org.json.JSONObject;
import ru.digital.services.sp.Props;

public class Auth {

    public static String clientId = Props.getOrEnv("client.id");
    public static String url = Props.getOrEnv("backend.url");
    public static String username = Props.getOrEnv("test.login");
    public static String default_password = Props.getOrEnv("test.password");
    public static String realm = Props.getOrEnv("realm");
    public static String commonToken = Props.getOrEnv("authorization.token.basic");


    public static Response getCode(String login, String password) {
       return
           given()
                   .baseUri(url)
                   .contentType("application/json")
                   .body(new JSONObject()
                           .put("client_id", clientId)
                           .put("realm", realm)
                           .put("consentPersonalData", true)
                           .put("grant_type", "password")
                           .put("username", login == null ? username : login)
                           .put("password", password == null ? default_password : password)
                           .toString())
                   .post("api/auth/login/");
    }

    public static String parseForOAuth2Token(Response response) {
        return response.jsonPath().getString("access_token");
    }

    public static String getToken(String login, String password) {
        Response response = getCode(login, password);
        return parseForOAuth2Token(response);
    }

    public static String getToken()
    {
        Response response = getCode(null, null);
        return parseForOAuth2Token(response);
    }
}